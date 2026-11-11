package com.thejaxonhill.dnd5eapi.spellcasting.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.spellcasting.application.SpellQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class SpellTools(
    private val queries: SpellQueries
) {
    @Tool
    fun getSpells(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getSpell(index: String) = queries.byIndex(index)
}