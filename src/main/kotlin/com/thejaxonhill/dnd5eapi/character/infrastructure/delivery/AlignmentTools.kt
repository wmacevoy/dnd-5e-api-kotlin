package com.thejaxonhill.dnd5eapi.character.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.character.application.AlignmentQueries
import com.thejaxonhill.dnd5eapi.character.domain.AlignmentExample
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class AlignmentTools(
    private val queries: AlignmentQueries
) {
    @Tool
    fun getAlignments(page: Page, example: AlignmentExample?) = queries.list(page, example)

    @Tool
    fun getAlignment(index: String) = queries.byIndex(index)
}