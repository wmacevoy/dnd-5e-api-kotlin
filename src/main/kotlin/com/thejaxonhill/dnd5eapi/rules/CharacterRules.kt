package com.thejaxonhill.dnd5eapi.rules

import com.thejaxonhill.dnd5eapi.compendium.CompendiumQueries
import com.thejaxonhill.dnd5eapi.compendium.KINDS

private val CLASSES = KINDS.single { it.name == "classes" }

/**
 * Character Rules' questions. It reads classes through the Compendium's query service, which REST and
 * MCP both use, so the same question gets the same answer on each (REQ2/SHAPE-6). An unknown class is
 * `NoSuchElementException`, as for any entry.
 */
class CharacterRules(private val queries: CompendiumQueries) {
    fun multiclassing(index: String, scores: AbilityScores): Eligibility =
        evaluate(multiclassingOf(index, queries.entry(CLASSES, index)), scores)
}
