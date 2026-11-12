package com.thejaxonhill.dnd5eapi

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.IntegerSchema
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

/** The REST API (REQ3/API-1, REQ1/API-2 to API-6), generated from [RESOURCES]. */
@Configuration
class RestApi(
    @Qualifier("requestMappingHandlerMapping") private val mapping: RequestMappingHandlerMapping,
    private val store: Store,
) {
    @PostConstruct
    fun registerRoutes() {
        for (resource in RESOURCES) {
            val endpoint = Endpoint(resource, store)
            val list = if (resource.listedByName) Endpoint::listByName else Endpoint::list
            route("/api/${resource.path}", endpoint, list.javaMethod!!)
            if (resource.searchable) route("/api/${resource.path}/search", endpoint, Endpoint::search.javaMethod!!)
            route("/api/${resource.path}/{index}", endpoint, Endpoint::get.javaMethod!!)
            if (resource.multiclassingTool != null) {
                route("/api/${resource.path}/{index}/multiclassing", endpoint, Endpoint::multiclassing.javaMethod!!)
            }
        }
    }

    private fun route(path: String, endpoint: Endpoint, method: Method) {
        val info = RequestMappingInfo.paths(path)
            .methods(RequestMethod.GET)
            .options(mapping.builderConfiguration)
            .build()
        mapping.registerMapping(info, endpoint, method)
    }

    /** springdoc finds only annotated controllers, so the document's paths are written from the registry (REQ1/DOC-1). */
    @Bean
    fun registryPaths() = OpenApiCustomizer { api ->
        for (resource in RESOURCES) {
            val filters = resource.filters.map {
                query(it.name, if (it.isInteger) IntegerSchema() else StringSchema())
            }
            api.path(
                "/api/${resource.path}",
                get(resource, "Get a paginated list of ${resource.plural}", PAGING + filters),
            )
            if (resource.searchable) {
                api.path(
                    "/api/${resource.path}/search",
                    get(resource, "Get a paginated list of ${resource.plural}", PAGING + query("query", StringSchema())),
                )
            }
            val index = Parameter().name("index").`in`("path").required(true).schema(StringSchema())
            api.path("/api/${resource.path}/{index}", get(resource, "Get ${resource.singular} by index", listOf(index)))
            if (resource.multiclassingTool != null) {
                val abilities = ABILITIES.keys.map { query(it, IntegerSchema()).required(true) }
                api.path(
                    "/api/${resource.path}/{index}/multiclassing",
                    get(resource, "Check ${resource.singular} multiclassing prerequisites", listOf(index) + abilities),
                )
            }
        }
    }

    private fun get(resource: Resource, summary: String, parameters: List<Parameter>) = PathItem().get(
        Operation()
            .addTagsItem(resource.tag)
            .summary(summary)
            .parameters(parameters)
            .responses(ApiResponses().addApiResponse("200", ApiResponse().description("OK"))),
    )

    private fun query(name: String, schema: io.swagger.v3.oas.models.media.Schema<*>) =
        Parameter().name(name).`in`("query").schema(schema)

    private val PAGING = listOf(
        query("page", IntegerSchema()),
        query("size", IntegerSchema()),
        query("sort", ArraySchema().items(StringSchema())),
    )
}

/** The handlers for one resource. Not a bean: [RestApi] makes one per row and registers its routes. */
@ResponseBody
class Endpoint(private val resource: Resource, private val store: Store) {
    fun list(pageable: Pageable, @RequestParam parameters: Map<String, String>): Paged {
        val items = store.find(resource, filters(parameters), pageable)
        return paged(items.map { item(resource, it) }, pageable)
    }

    fun listByName(
        @PageableDefault(size = 10, sort = ["name"]) pageable: Pageable,
        @RequestParam parameters: Map<String, String>,
    ) = list(pageable, parameters)

    fun search(
        @PageableDefault(size = 10, sort = ["name"]) pageable: Pageable,
        @RequestParam(required = false) query: String?,
    ): Paged {
        val items = store.findByName(resource, query ?: "", pageable)
        return paged(items.map { item(resource, it) }, pageable)
    }

    fun get(@PathVariable index: String): Map<String, Any?> =
        store.findByIndex(resource, index)?.let { item(resource, it) } ?: throw NoSuchElementException()

    /** Whether the six scores in the request meet the class's multiclassing prerequisites (EXPERIMENT.md change 4). */
    fun multiclassing(@PathVariable index: String, @RequestParam parameters: Map<String, String>): Map<String, Any?> {
        val document = store.findByIndex(resource, index) ?: throw NoSuchElementException()
        return multiclassingView(index, eligibility(document, scores(parameters)))
    }

    /** The resource's filters that the request names; other parameters are ignored. */
    private fun filters(parameters: Map<String, String>): Map<String, Any> {
        val result = LinkedHashMap<String, Any>()
        for (filter in resource.filters) {
            val value = parameters[filter.name] ?: continue
            if (!filter.isInteger) {
                result[filter.name] = value
            } else if (value.isNotBlank()) {
                // A checked exception, so the handler answers 500, as a failed binding did before.
                result[filter.name] = value.toIntOrNull()
                    ?: throw ServletRequestBindingException("Filter '${filter.name}' must be a whole number, not '$value'")
            }
        }
        return result
    }
}
