package com.thejaxonhill.dnd5eapi.rules.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.rules.application.usecase.LoadDamageTypeByIndex
import com.thejaxonhill.dnd5eapi.rules.application.usecase.LoadDamageTypes
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class DamageTypeTools(
    private val loadDamageTypeByIndex: LoadDamageTypeByIndex,
    private val loadDamageTypes: LoadDamageTypes
) {
    @Tool
    fun getDamageTypes(pageNumber: Int? = 1, pageSize: Int? = 20) =
        loadDamageTypes.load(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getDamageType(index: String) = loadDamageTypeByIndex.load(index)
}