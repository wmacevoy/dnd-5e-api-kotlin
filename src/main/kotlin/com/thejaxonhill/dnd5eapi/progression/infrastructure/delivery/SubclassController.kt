package com.thejaxonhill.dnd5eapi.progression.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPage
import com.thejaxonhill.dnd5eapi.progression.application.dto.SubclassView
import com.thejaxonhill.dnd5eapi.progression.application.usecase.LoadSubclassByIndex
import com.thejaxonhill.dnd5eapi.progression.application.usecase.LoadSubclasses
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassExample
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Subclasses")
@RestController
@RequestMapping("/api/subclasses")
class SubclassController(
    private val loadSubclassByIndex: LoadSubclassByIndex,
    private val loadSubclasses: LoadSubclasses
) {
    @Operation(summary = "Get a paginated list of subclasses")
    @GetMapping
    fun getSubclasses(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: SubclassExample?
    ): ResponseEntity<PagedModel<SubclassView>> =
        ResponseEntity.ok(loadSubclasses.load(pageable.toPage(), example))

    @Operation(summary = "Get a subclass by index")
    @GetMapping("/{index}")
    fun getSubclass(@PathVariable index: String): ResponseEntity<SubclassView> =
        ResponseEntity.ok(loadSubclassByIndex.load(index))
}