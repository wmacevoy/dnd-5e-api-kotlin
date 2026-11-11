package com.thejaxonhill.dnd5eapi.progression.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.progression.application.SubclassQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class SubclassTools(
    private val queries: SubclassQueries
) {
    @Tool
    fun getSubclasses(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getSubclass(index: String) = queries.byIndex(index)
}