package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.SubraceQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPage
import com.thejaxonhill.dnd5eapi.character.application.dto.SubraceView
import com.thejaxonhill.dnd5eapi.character.domain.SubraceExample
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Subraces")
@RestController
@RequestMapping("/api/subraces")
class SubraceController(
    private val queries: SubraceQueries
) {
    @Operation(summary = "Get a paginated list of subraces")
    @GetMapping
    fun getSubraces(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: SubraceExample?
    ): ResponseEntity<PagedModel<SubraceView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a subrace by index")
    @GetMapping("/{index}")
    fun getSubrace(@PathVariable index: String): ResponseEntity<SubraceView> =
        ResponseEntity.ok(queries.byIndex(index))
}