package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.FeatQueries
import com.thejaxonhill.dnd5eapi.character.application.dto.FeatView
import com.thejaxonhill.dnd5eapi.character.domain.FeatExample
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

@Tag(name = "Feats")
@RestController
@RequestMapping("/api/feats")
class FeatController(
    private val queries: FeatQueries
) {
    @Operation(summary = "Get a paginated list of feats")
    @GetMapping
    fun getFeats(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: FeatExample?
    ): ResponseEntity<PagedModel<FeatView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a feat by index")
    @GetMapping("/{index}")
    fun getFeat(@PathVariable index: String): ResponseEntity<FeatView> =
        ResponseEntity.ok(queries.byIndex(index))
}