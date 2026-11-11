package com.thejaxonhill.dnd5eapi.progression.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.progression.application.usecase.LoadLevelByIndex
import com.thejaxonhill.dnd5eapi.progression.application.usecase.LoadLevels
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class LevelTools(
    private val loadLevelByIndex: LoadLevelByIndex,
    private val loadLevels: LoadLevels
) {
    @Tool
    fun getLevels(pageNumber: Int? = 1, pageSize: Int? = 20) =
        loadLevels.load(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getLevel(index: String) = loadLevelByIndex.load(index)
}