package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.SkillQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPage
import com.thejaxonhill.dnd5eapi.character.application.dto.SkillView
import com.thejaxonhill.dnd5eapi.character.domain.SkillExample
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Skills")
@RestController
@RequestMapping("/api/skills")
class SkillController(
    private val queries: SkillQueries
) {
    @Operation(summary = "Get a paginated list of skills")
    @GetMapping
    fun getSkills(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: SkillExample?
    ): ResponseEntity<PagedModel<SkillView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a skill by index")
    @GetMapping("/{index}")
    fun getSkill(@PathVariable index: String): ResponseEntity<SkillView> =
        ResponseEntity.ok(queries.byIndex(index))
}