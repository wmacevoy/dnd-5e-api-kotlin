package com.thejaxonhill.dnd5eapi.rules.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.rules.application.ConditionQueries
import com.thejaxonhill.dnd5eapi.rules.application.dto.ConditionView
import com.thejaxonhill.dnd5eapi.rules.domain.ConditionExample
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

@Tag(name = "Conditions")
@RestController
@RequestMapping("/api/conditions")
class ConditionController(
    private val queries: ConditionQueries
) {
    @Operation(summary = "Get a paginated list of conditions")
    @GetMapping
    fun getConditions(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: ConditionExample?
    ): ResponseEntity<PagedModel<ConditionView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a condition by index")
    @GetMapping("/{index}")
    fun getCondition(@PathVariable index: String): ResponseEntity<ConditionView> =
        ResponseEntity.ok(queries.byIndex(index))
}