package com.thejaxonhill.dnd5eapi.adapter.rest

import com.thejaxonhill.dnd5eapi.adapter.published.Paged
import com.thejaxonhill.dnd5eapi.adapter.published.paged
import com.thejaxonhill.dnd5eapi.adapter.published.published
import com.thejaxonhill.dnd5eapi.compendium.CompendiumQueries
import com.thejaxonhill.dnd5eapi.compendium.KINDS
import com.thejaxonhill.dnd5eapi.compendium.Kind
import com.thejaxonhill.dnd5eapi.compendium.Order
import com.thejaxonhill.dnd5eapi.compendium.PageRequest
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import jakarta.annotation.PostConstruct
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import kotlin.reflect.jvm.javaMethod

/** Kinds whose REST list sorts by name with 10 per page, where the others are unsorted with 20 (REQ1/API-5). */
private val LISTED_BY_NAME = setOf("equipment")

/** The REST adapter (REQ1/API-1 to API-6): two routes per kind, plus search where a kind allows it. */
@Configuration
class RestApi(
    @Qualifier("requestMappingHandlerMapping") private val mapping: RequestMappingHandlerMapping,
    private val queries: CompendiumQueries,
) {
    @PostConstruct
    fun registerRoutes() {
        for (kind in KINDS) {
            val endpoint = Endpoint(kind, queries)
            val list = if (kind.name in LISTED_BY_NAME) Endpoint::listByName else Endpoint::list
            route("/api/${kind.name}", endpoint, list.javaMethod!!)
            if (kind.searchableByName) route("/api/${kind.name}/search", endpoint, Endpoint::search.javaMethod!!)
            route("/api/${kind.name}/{index}", endpoint, Endpoint::get.javaMethod!!)
        }
    }

    private fun route(path: String, endpoint: Endpoint, method: Method) {
        val info = RequestMappingInfo.paths(path)
            .methods(RequestMethod.GET)
            .options(mapping.builderConfiguration)
            .build()
        mapping.registerMapping(info, endpoint, method)
    }

    /** springdoc finds only annotated controllers, so the document's paths are written from the kinds (REQ1/DOC-1). */
    @Bean
    fun kindPaths() = OpenApiCustomizer { api ->
        for (kind in KINDS) {
            val filters = kind.filters.map { query(it.field, if (it.isInteger) IntegerSchema() else StringSchema()) }
            val listSummary = "Get a paginated list of ${kind.plural}"
            api.path("/api/${kind.name}", get(kind, listSummary, PAGING + filters))
            if (kind.searchableByName) {
                api.path("/api/${kind.name}/search", get(kind, listSummary, PAGING + query("query", StringSchema())))
            }
            val index = Parameter().name("index").`in`("path").required(true).schema(StringSchema())
            val one = listOf(kind.article, kind.singular).filter { it.isNotEmpty() }.joinToString(" ")
            api.path("/api/${kind.name}/{index}", get(kind, "Get $one by index", listOf(index)))
        }
    }

    private fun get(kind: Kind, summary: String, parameters: List<Parameter>) = PathItem().get(
        Operation()
            .addTagsItem(titleCase(kind.plural))
            .summary(summary)
            .parameters(parameters)
            .responses(ApiResponses().addApiResponse("200", ApiResponse().description("OK"))),
    )

    private fun query(name: String, schema: Schema<*>) = Parameter().name(name).`in`("query").schema(schema)

    private val PAGING = listOf(
        query("page", IntegerSchema()),
        query("size", IntegerSchema()),
        query("sort", ArraySchema().items(StringSchema())),
    )
}

private fun titleCase(words: String) = words.split(' ').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

/** The handlers for one kind. Not a bean: [RestApi] makes one per kind and registers its routes. */
@ResponseBody
class Endpoint(private val kind: Kind, private val queries: CompendiumQueries) {
    fun list(pageable: Pageable, @RequestParam parameters: Map<String, String>): Paged =
        paged(queries.entries(kind, filter(parameters), page(pageable))) { published(kind, it) }

    fun listByName(
        @PageableDefault(size = 10, sort = ["name"]) pageable: Pageable,
        @RequestParam parameters: Map<String, String>,
    ) = list(pageable, parameters)

    fun search(
        @PageableDefault(size = 10, sort = ["name"]) pageable: Pageable,
        @RequestParam(required = false) query: String?,
    ): Paged = paged(queries.entriesNamed(kind, query ?: "", page(pageable))) { published(kind, it) }

    fun get(@PathVariable index: String): Map<String, Any?> = published(kind, queries.entry(kind, index))

    /** The kind's filters that the request names; other parameters are ignored. */
    private fun filter(parameters: Map<String, String>): Map<String, Any> {
        val result = LinkedHashMap<String, Any>()
        for (filter in kind.filters) {
            val value = parameters[filter.field] ?: continue
            if (!filter.isInteger) {
                result[filter.field] = value
            } else if (value.isNotBlank()) {
                // A checked exception, so the handler answers 500, as a failed binding did before.
                result[filter.field] = value.toIntOrNull()
                    ?: throw ServletRequestBindingException("Filter '${filter.field}' must be a whole number, not '$value'")
            }
        }
        return result
    }
}

private fun page(pageable: Pageable) = PageRequest(
    pageable.pageNumber,
    pageable.pageSize,
    pageable.sort.map { Order(it.property, it.direction == Sort.Direction.DESC) }.toList(),
)
