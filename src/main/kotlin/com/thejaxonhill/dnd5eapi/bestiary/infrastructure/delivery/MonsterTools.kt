package com.thejaxonhill.dnd5eapi.bestiary.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.bestiary.application.MonsterQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class MonsterTools(
    private val queries: MonsterQueries
) {
    @Tool
    fun getMonsters(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getMonster(index: String) = queries.byIndex(index)
}