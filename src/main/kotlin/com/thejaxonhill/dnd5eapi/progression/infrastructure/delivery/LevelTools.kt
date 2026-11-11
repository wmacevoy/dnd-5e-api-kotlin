package com.thejaxonhill.dnd5eapi.progression.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.progression.application.LevelQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class LevelTools(
    private val queries: LevelQueries
) {
    @Tool
    fun getLevels(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getLevel(index: String) = queries.byIndex(index)
}