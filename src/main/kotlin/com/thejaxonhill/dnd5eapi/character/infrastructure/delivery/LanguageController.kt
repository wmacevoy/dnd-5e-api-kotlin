package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.LanguageQueries
import com.thejaxonhill.dnd5eapi.character.application.dto.LanguageView
import com.thejaxonhill.dnd5eapi.character.domain.LanguageExample
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

@Tag(name = "Languages")
@RestController
@RequestMapping("/api/languages")
class LanguageController(
    private val queries: LanguageQueries
) {
    @Operation(summary = "Get a paginated list of languages")
    @GetMapping
    fun getLanguages(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: LanguageExample?
    ): ResponseEntity<PagedModel<LanguageView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a language by index")
    @GetMapping("/{index}")
    fun getLanguage(@PathVariable index: String): ResponseEntity<LanguageView> =
        ResponseEntity.ok(queries.byIndex(index))
}