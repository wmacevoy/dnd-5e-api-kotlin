package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import com.thejaxonhill.dnd5eapi.character.application.usecase.LoadTraitByIndex
import com.thejaxonhill.dnd5eapi.character.application.usecase.LoadTraits
import org.springframework.ai.tool.annotation.Tool

@McpTools
class TraitTools(
    private val loadTraitByIndex: LoadTraitByIndex,
    private val loadTraits: LoadTraits
) {
    @Tool
    fun getTraits(pageNumber: Int? = 1, pageSize: Int? = 20) =
        loadTraits.load(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getTrait(index: String) = loadTraitByIndex.load(index)
}