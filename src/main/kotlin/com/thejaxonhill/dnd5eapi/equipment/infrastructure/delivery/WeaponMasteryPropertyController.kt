package com.thejaxonhill.dnd5eapi.equipment.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.equipment.application.WeaponMasteryPropertyQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPage
import com.thejaxonhill.dnd5eapi.equipment.application.dto.WeaponMasteryPropertyView
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponMasteryPropertyExample
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Weapon Mastery Properties")
@RestController
@RequestMapping("/api/weapon-mastery-properties")
class WeaponMasteryPropertyController(
    private val queries: WeaponMasteryPropertyQueries
) {
    @Operation(summary = "Get a paginated list of weapon mastery properties")
    @GetMapping
    fun getWeaponMasteryProperties(
        @ParameterObject pageable: Pageable,
        @ParameterObject example: WeaponMasteryPropertyExample?
    ): ResponseEntity<PagedModel<WeaponMasteryPropertyView>> =
        ResponseEntity.ok(queries.list(pageable.toPage(), example))

    @Operation(summary = "Get a weapon mastery property by index")
    @GetMapping("/{index}")
    fun getWeaponMasteryProperty(@PathVariable index: String): ResponseEntity<WeaponMasteryPropertyView> =
        ResponseEntity.ok(queries.byIndex(index))
}