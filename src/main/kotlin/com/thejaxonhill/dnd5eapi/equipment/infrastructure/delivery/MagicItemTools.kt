package com.thejaxonhill.dnd5eapi.equipment.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.equipment.application.MagicItemQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class MagicItemTools(
    private val queries: MagicItemQueries
) {
    @Tool
    fun getMagicItems(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getMagicItem(index: String) = queries.byIndex(index)
}