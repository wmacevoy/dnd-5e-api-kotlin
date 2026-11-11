package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.TraitQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class TraitTools(
    private val queries: TraitQueries
) {
    @Tool
    fun getTraits(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getTrait(index: String) = queries.byIndex(index)
}