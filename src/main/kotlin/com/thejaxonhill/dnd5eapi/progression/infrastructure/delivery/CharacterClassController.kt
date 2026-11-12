package com.thejaxonhill.dnd5eapi.progression.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.progression.application.CharacterClassQueries
import com.thejaxonhill.dnd5eapi.progression.application.dto.CharacterClassView
import com.thejaxonhill.dnd5eapi.progression.application.dto.MultiClassingCheckView
import com.thejaxonhill.dnd5eapi.progression.domain.CharacterClassExample
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Character Classes")
@RestController
@RequestMapping("/api/classes")
class CharacterClassController(
    private val queries: CharacterClassQueries
) {
    @Operation(summary = "Get a paginated list of character classes")
    @GetMapping
    fun getClasses(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: CharacterClassExample?
    ): ResponseEntity<PagedModel<CharacterClassView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a character class by index")
    @GetMapping("/{index}")
    fun getCharacterClass(@PathVariable index: String): ResponseEntity<CharacterClassView> =
        ResponseEntity.ok(queries.byIndex(index))

    // Scores are nullable so that the domain refuses a missing one, and the handler answers 400.
    @Operation(summary = "Check whether six ability scores meet a class's multiclassing prerequisites")
    @GetMapping("/{index}/multiclassing-check")
    fun checkMultiClassing(
        @PathVariable index: String,
        @RequestParam("str") str: Int?,
        @RequestParam("dex") dex: Int?,
        @RequestParam("con") con: Int?,
        @RequestParam("int") int: Int?,
        @RequestParam("wis") wis: Int?,
        @RequestParam("cha") cha: Int?,
    ): ResponseEntity<MultiClassingCheckView> = ResponseEntity.ok(
        queries.multiClassingCheck(index, mapOf("str" to str, "dex" to dex, "con" to con, "int" to int, "wis" to wis, "cha" to cha))
    )
}