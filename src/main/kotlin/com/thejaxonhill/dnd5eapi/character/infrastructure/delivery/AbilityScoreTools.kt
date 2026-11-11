package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.AbilityScoreQueries
import com.thejaxonhill.dnd5eapi.character.domain.AbilityScoreExample
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class AbilityScoreTools(
    private val queries: AbilityScoreQueries
) {
    @Tool
    fun getAbilityScores(page: Page, abilityScoreExample: AbilityScoreExample? = null) =
        queries.list(page, abilityScoreExample)

    @Tool
    fun getAbilityScore(index: String) = queries.byIndex(index)
}