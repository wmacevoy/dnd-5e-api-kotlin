package com.thejaxonhill.dnd5eapi.adapter.mcp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.thejaxonhill.dnd5eapi.adapter.published.objectsAt
import com.thejaxonhill.dnd5eapi.adapter.published.paged
import com.thejaxonhill.dnd5eapi.adapter.published.published
import com.thejaxonhill.dnd5eapi.compendium.CompendiumQueries
import com.thejaxonhill.dnd5eapi.compendium.Entry
import com.thejaxonhill.dnd5eapi.compendium.Filter
import com.thejaxonhill.dnd5eapi.compendium.KINDS
import com.thejaxonhill.dnd5eapi.compendium.Kind
import com.thejaxonhill.dnd5eapi.compendium.Order
import com.thejaxonhill.dnd5eapi.compendium.PageRequest
import com.thejaxonhill.dnd5eapi.rules.ABILITIES
import com.thejaxonhill.dnd5eapi.rules.AbilityScores
import com.thejaxonhill.dnd5eapi.rules.CharacterRules
import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.definition.ToolDefinition
import org.springframework.ai.tool.execution.ToolExecutionException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort

/** The MCP adapter (REQ3/MCP-1): a list tool and a get tool for each kind. */
@Configuration
class McpTools {
    @Bean
    fun tools(queries: CompendiumQueries, rules: CharacterRules): ToolCallbackProvider =
        ToolCallbackProvider.from(KINDS.flatMap { listOf(listTool(it, queries), getTool(it, queries)) } + multiclassingTool(rules))
}

/** EXPERIMENT/CHANGE-4: the question the REST route answers, with the six scores as arguments. */
private fun multiclassingTool(rules: CharacterRules) = Tool(
    "checkMulticlassing",
    objectSchema(mapOf("index" to mapOf("type" to "string")) + ABILITIES.associateWith { INTEGER }, closed = true),
) { arguments ->
    val scores = AbilityScores(ABILITIES.associateWith { integer(arguments[it]) })
    published(rules.multiclassing(arguments["index"] as String, scores))
}

/** Tool names follow `get` + the kind's nouns, except these, which were named otherwise and are published. */
private val LIST_TOOL_NAMES = mapOf("classes" to "getClasses", "equipment" to "getAllEquipment")

/**
 * Two kinds' list tools take a `page` object and a filter object, named here. The rest take
 * `pageNumber` (default 1, so the first page is skipped) and `pageSize` (default 20), and no filter
 * (queue item 11).
 */
private val PAGE_AND_EXAMPLE = mapOf("ability-scores" to "abilityScoreExample", "alignments" to "example")

private fun listToolName(kind: Kind) = LIST_TOOL_NAMES[kind.name] ?: "get" + pascalCase(kind.plural)

private fun getToolName(kind: Kind) = "get" + pascalCase(kind.singular)

private fun pascalCase(words: String) = words.split(' ').joinToString("") { it.replaceFirstChar(Char::uppercase) }

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

/** An entry as MCP clients receive it: the published entry, with [McpNulls] added. */
private fun mcpEntry(kind: Kind, entry: Entry): Map<String, Any?> {
    val item = published(kind, entry)
    for (nulls in MCP_NULLS[kind.name].orEmpty()) {
        for (target in objectsAt(item, nulls.at)) {
            val where = nulls.onlyWhere
            if (where != null && target[where.first] != where.second) continue
            for (field in nulls.fields) target.putIfAbsent(field, null)
        }
    }
    return item
}

private fun getTool(kind: Kind, queries: CompendiumQueries) = Tool(
    getToolName(kind),
    objectSchema(mapOf("index" to mapOf("type" to "string")), closed = true),
) { arguments -> mcpEntry(kind, queries.entry(kind, arguments["index"] as String)) }

private fun listTool(kind: Kind, queries: CompendiumQueries): Tool {
    val exampleName = PAGE_AND_EXAMPLE[kind.name]
    if (exampleName == null) {
        return Tool(
            listToolName(kind),
            objectSchema(mapOf("pageNumber" to INTEGER, "pageSize" to INTEGER), closed = true),
        ) { arguments ->
            val page = PageRequest(integer(arguments["pageNumber"]) ?: 1, integer(arguments["pageSize"]) ?: 20)
            list(kind, queries, emptyMap(), page)
        }
    }
    val pageSchema = objectSchema(
        mapOf("page" to INT32, "size" to INT32, "sort" to mapOf("type" to "array", "items" to mapOf("type" to "string"))),
    )
    val exampleSchema = objectSchema(kind.filters.sortedBy { it.field }.associate { it.field to filterSchema(it) })
    return Tool(
        listToolName(kind),
        objectSchema(mapOf("page" to pageSchema, exampleName to exampleSchema), closed = true),
    ) { arguments ->
        @Suppress("UNCHECKED_CAST")
        val page = arguments["page"] as? Map<String, Any?> ?: throw IllegalArgumentException("${listToolName(kind)} needs a page")
        @Suppress("UNCHECKED_CAST")
        val sort = (page["sort"] as List<String>?) ?: emptyList()
        val request = PageRequest(integer(page["page"]) ?: 0, integer(page["size"]) ?: 10, orderOf(sort))
        val example = arguments[exampleName] as? Map<*, *> ?: emptyMap<String, Any?>()
        val filter = kind.filters.mapNotNull { f -> example[f.field]?.let { f.field to it } }.toMap()
        list(kind, queries, filter, request)
    }
}

private fun list(kind: Kind, queries: CompendiumQueries, filter: Map<String, Any>, page: PageRequest) =
    paged(queries.entries(kind, filter, page)) { mcpEntry(kind, it) }

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

/** `field` or `field,direction`, as a REST `sort` parameter is written. */
private fun orderOf(clauses: List<String>): List<Order> = clauses.map { it.trim() }.filter { it.isNotBlank() }.map { clause ->
    val parts = clause.split(',', limit = 2).map { it.trim() }
    val direction = parts.getOrNull(1)?.let { Sort.Direction.fromString(it) }
    Order(parts[0], direction == Sort.Direction.DESC)
}
