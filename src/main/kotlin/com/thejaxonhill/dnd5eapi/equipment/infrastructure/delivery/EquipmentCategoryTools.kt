package com.thejaxonhill.dnd5eapi.equipment.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.equipment.application.EquipmentCategoryQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class EquipmentCategoryTools(
    private val queries: EquipmentCategoryQueries
) {
    @Tool
    fun getEquipmentCategories(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getEquipmentCategory(index: String) = queries.byIndex(index)
}