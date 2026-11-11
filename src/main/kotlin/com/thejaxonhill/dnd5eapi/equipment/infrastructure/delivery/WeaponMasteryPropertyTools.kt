package com.thejaxonhill.dnd5eapi.equipment.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.equipment.application.WeaponMasteryPropertyQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class WeaponMasteryPropertyTools(
    private val queries: WeaponMasteryPropertyQueries
) {
    @Tool
    fun getWeaponMasteryProperties(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getWeaponMasteryProperty(index: String) = queries.byIndex(index)
}