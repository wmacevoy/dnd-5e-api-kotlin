package com.thejaxonhill.dnd5eapi.rules.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.rules.application.RuleQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class RuleTools(
    private val queries: RuleQueries
) {
    @Tool
    fun getRules(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getRule(index: String) = queries.byIndex(index)
}