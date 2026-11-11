package com.thejaxonhill.dnd5eapi.rules.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.rules.application.RuleQueries
import com.thejaxonhill.dnd5eapi.rules.application.dto.RuleView
import com.thejaxonhill.dnd5eapi.rules.domain.RuleExample
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPage
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Rules")
@RestController
@RequestMapping("/api/rules")
class RuleController(
    private val queries: RuleQueries
) {
    @Operation(summary = "Get a paginated list of rules")
    @GetMapping
    fun getRules(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: RuleExample?
    ): ResponseEntity<PagedModel<RuleView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a rule by index")
    @GetMapping("/{index}")
    fun getRule(@PathVariable index: String): ResponseEntity<RuleView> =
        ResponseEntity.ok(queries.byIndex(index))
}