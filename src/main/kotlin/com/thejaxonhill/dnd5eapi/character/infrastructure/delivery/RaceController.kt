package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.RaceQueries
import com.thejaxonhill.dnd5eapi.character.application.dto.RaceView
import com.thejaxonhill.dnd5eapi.character.domain.RaceExample
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

@Tag(name = "Races")
@RestController
@RequestMapping("/api/races")
class RaceController(
    private val queries: RaceQueries
) {
    @Operation(summary = "Get a paginated list of races")
    @GetMapping
    fun getRaces(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: RaceExample?
    ): ResponseEntity<PagedModel<RaceView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a race by index")
    @GetMapping("/{index}")
    fun getRace(@PathVariable index: String): ResponseEntity<RaceView> =
        ResponseEntity.ok(queries.byIndex(index))
}