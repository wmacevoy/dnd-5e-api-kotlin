package com.thejaxonhill.dnd5eapi

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.definition.ToolDefinition
import org.springframework.ai.tool.execution.ToolExecutionException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/** The MCP tools (REQ1/MCP-1): a list and a get-by-index for each row of [RESOURCES]. */
@Configuration
class McpTools {
    @Bean
    fun tools(store: Store): ToolCallbackProvider =
        ToolCallbackProvider.from(RESOURCES.flatMap { listOf(listTool(it, store), getTool(it, store)) })
}

// Includes nulls, unlike Spring's REST mapper; MCP results have always carried them.
private val json = jacksonObjectMapper()

/** A tool whose arguments arrive as a JSON object and whose result is returned as JSON. */
private class Tool(
    name: String,
    inputSchema: Map<String, Any>,
    private val run: (Map<String, Any?>) -> Any,
) : ToolCallback {
    // The description has always been the tool's name.
    private val definition = ToolDefinition.builder()
        .name(name)
        .description(name)
        .inputSchema(json.writeValueAsString(inputSchema))
        .build()

    override fun getToolDefinition() = definition

    override fun call(toolInput: String): String {
        val result = try {
            run(json.readValue<Map<String, Any?>>(toolInput))
        } catch (failure: Exception) {
            // MCP reports this as an error result whose text is the cause's message.
            throw ToolExecutionException(definition, failure)
        }
        return json.writeValueAsString(result)
    }

    override fun call(toolInput: String, toolContext: ToolContext?) = call(toolInput)
}

private fun getTool(resource: Resource, store: Store) = Tool(
    resource.getTool,
    objectSchema(mapOf("index" to mapOf("type" to "string")), closed = true),
) { arguments ->
    val document = store.findByIndex(resource, arguments["index"] as String) ?: throw NoSuchElementException()
    mcpItem(resource, document)
}

private fun listTool(resource: Resource, store: Store) = when (val shape = resource.listArguments) {
    is PageNumberAndSize -> Tool(
        resource.listTool,
        objectSchema(mapOf("pageNumber" to INTEGER, "pageSize" to INTEGER), closed = true),
    ) { arguments ->
        val pageable = PageRequest.of(integer(arguments["pageNumber"]) ?: 1, integer(arguments["pageSize"]) ?: 20)
        list(resource, store, emptyMap(), pageable)
    }

    is PageAndExample -> Tool(
        resource.listTool,
        objectSchema(
            mapOf(
                "page" to objectSchema(
                    mapOf(
                        "page" to INT32,
                        "size" to INT32,
                        "sort" to mapOf("type" to "array", "items" to mapOf("type" to "string")),
                    ),
                ),
                shape.exampleName to objectSchema(
                    resource.filters.sortedBy { it.name }.associate { it.name to filterSchema(it) },
                ),
            ),
            closed = true,
        ),
    ) { arguments ->
        @Suppress("UNCHECKED_CAST")
        val page = arguments["page"] as? Map<String, Any?>
            ?: throw IllegalArgumentException("${resource.listTool} needs a page")
        @Suppress("UNCHECKED_CAST")
        val sort = (page["sort"] as List<String>?) ?: emptyList()
        val pageable = PageRequest.of(integer(page["page"]) ?: 0, integer(page["size"]) ?: 10, sortOf(sort))
        val example = arguments[shape.exampleName] as? Map<*, *> ?: emptyMap<String, Any?>()
        val filters = resource.filters.mapNotNull { filter -> example[filter.name]?.let { filter.name to it } }.toMap()
        list(resource, store, filters, pageable)
    }
}

private fun list(resource: Resource, store: Store, filters: Map<String, Any>, pageable: Pageable) =
    paged(store.find(resource, filters, pageable).map { mcpItem(resource, it) }, pageable)

private val INTEGER = mapOf("type" to "integer")
private val INT32 = mapOf("type" to "integer", "format" to "int32")

private fun filterSchema(filter: Filter) = if (filter.isInteger) INT32 else mapOf("type" to "string")

/** Every property is required, in the order given, as in the schemas Spring AI generated before. */
private fun objectSchema(properties: Map<String, Any>, closed: Boolean = false): Map<String, Any> {
    val schema = linkedMapOf<String, Any>(
        "type" to "object",
        "properties" to properties,
        "required" to properties.keys.toList(),
    )
    if (closed) schema["additionalProperties"] = false
    return schema
}

private fun integer(value: Any?): Int? = when (value) {
    null -> null
    is Number -> value.toInt()
    else -> value.toString().toInt()
}

/** `property` or `property,direction`, as a REST `sort` parameter is written. */
private fun sortOf(clauses: List<String>): Sort {
    val orders = clauses.map { it.trim() }.filter { it.isNotBlank() }.map { clause ->
        val parts = clause.split(',', limit = 2).map { it.trim() }
        val direction = parts.getOrNull(1)?.let { Sort.Direction.fromString(it) } ?: Sort.Direction.ASC
        Sort.Order(direction, parts[0])
    }
    return if (orders.isEmpty()) Sort.unsorted() else Sort.by(orders)
}
