package com.thejaxonhill.dnd5eapi.progression.infrastructure.delivery

import com.thejaxonhill.dnd5eapi.progression.application.CharacterClassQueries
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.infrastructure.stereotype.McpTools
import org.springframework.ai.tool.annotation.Tool

@McpTools
class CharacterClassTools(
    private val queries: CharacterClassQueries
) {
    @Tool
    fun getClasses(pageNumber: Int? = 1, pageSize: Int? = 20) =
        queries.list(Page(pageNumber ?: 1, pageSize ?: 20), null)

    @Tool
    fun getCharacterClass(index: String) = queries.byIndex(index)

    @Tool
    fun checkMultiClassing(index: String, str: Int?, dex: Int?, con: Int?, int: Int?, wis: Int?, cha: Int?) =
        queries.multiClassingCheck(index, mapOf("str" to str, "dex" to dex, "con" to con, "int" to int, "wis" to wis, "cha" to cha))
}