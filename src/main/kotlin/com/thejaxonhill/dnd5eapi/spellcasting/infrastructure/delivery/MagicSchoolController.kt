package com.thejaxonhill.dnd5eapi.spellcasting.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.spellcasting.application.MagicSchoolQueries
import com.thejaxonhill.dnd5eapi.spellcasting.application.dto.MagicSchoolView
import com.thejaxonhill.dnd5eapi.spellcasting.domain.MagicSchoolExample
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

@Tag(name = "Magic Schools")
@RestController
@RequestMapping("/api/magic-schools")
class MagicSchoolController(
    private val queries: MagicSchoolQueries
) {
    @Operation(summary = "Get a paginated list of magic schools")
    @GetMapping
    fun getMagicSchools(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: MagicSchoolExample?
    ): ResponseEntity<PagedModel<MagicSchoolView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a magic school by index")
    @GetMapping("/{index}")
    fun getMagicSchool(@PathVariable index: String): ResponseEntity<MagicSchoolView> =
        ResponseEntity.ok(queries.byIndex(index))
}