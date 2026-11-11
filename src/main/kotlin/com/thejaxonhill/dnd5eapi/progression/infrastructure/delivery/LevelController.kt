package com.thejaxonhill.dnd5eapi.progression.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.progression.application.LevelQueries
import com.thejaxonhill.dnd5eapi.progression.application.dto.LevelView
import com.thejaxonhill.dnd5eapi.progression.domain.LevelExample
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

@Tag(name = "Levels")
@RestController
@RequestMapping("/api/levels")
class LevelController(
    private val queries: LevelQueries
) {
    @Operation(summary = "Get a paginated list of levels")
    @GetMapping
    fun getLevels(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: LevelExample?
    ): ResponseEntity<PagedModel<LevelView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a level by index")
    @GetMapping("/{index}")
    fun getLevel(@PathVariable index: String): ResponseEntity<LevelView> =
        ResponseEntity.ok(queries.byIndex(index))
}