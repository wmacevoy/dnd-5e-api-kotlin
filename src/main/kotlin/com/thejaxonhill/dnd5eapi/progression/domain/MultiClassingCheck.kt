package com.thejaxonhill.dnd5eapi.progression.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.OptionsArrayOptionSet
import com.thejaxonhill.dnd5eapi.shared.domain.model.ScorePrerequisiteOption

private val ABILITY_INDEXES = listOf("str", "dex", "con", "int", "wis", "cha")

// Invented bound: no requirement states the range of an ability score.
private val SCORE_RANGE = 1..30

data class MultiClassingCheck(
    val index: Index,
    val meetsPrerequisites: Boolean,
    val unmetPrerequisites: List<UnmetPrerequisite>,
)

data class UnmetPrerequisite(
    val abilityScore: ApiReference,
    val minimumScore: Int,
    val score: Int,
)

private class Requirement(val abilityScore: ApiReference, val minimumScore: Int)

/**
 * Whether the scores, keyed by ability index, meet this class's multiclassing prerequisites.
 *
 * Every `prerequisites` entry must be met. `prerequisiteOptions` must have at least `choose` of its
 * options met; when it does not, every option the character falls short of is listed, because any
 * one of them would have met it. Throws IllegalArgumentException for a missing or out-of-range
 * score, and IllegalStateException for class data this check cannot read.
 */
fun CharacterClass.checkMultiClassing(scores: Map<String, Int?>): MultiClassingCheck {
    val known = ABILITY_INDEXES.associateWith { ability ->
        val score = scores[ability] ?: throw IllegalArgumentException("Missing ability score '$ability'")
        require(score in SCORE_RANGE) { "Ability score '$ability' must be 1 to 30, was $score" }
        score
    }
    val rules = multiClassing ?: throw IllegalStateException("Class '${index.value}' has no multiclassing data")
    val choice = rules.prerequisiteOptions
    val required = rules.prerequisites.orEmpty().map { Requirement(it.abilityScore, it.minimumScore) }
    val options = when (val set = choice?.from) {
        null -> emptyList()
        is OptionsArrayOptionSet -> set.options.orEmpty().map { option ->
            val score = option as? ScorePrerequisiteOption
                ?: throw IllegalStateException("Class '${index.value}' has a multiclassing option that is not a score")
            Requirement(score.abilityScore, score.minimumScore)
        }
        else -> throw IllegalStateException("Class '${index.value}' has a multiclassing option set this check cannot read")
    }

    val requiredUnmet = required.filterNot { known.meets(it) }
    val optionsSatisfied = choice == null || options.count { known.meets(it) } >= choice.choose
    val optionsUnmet = if (optionsSatisfied) emptyList() else options.filterNot { known.meets(it) }

    return MultiClassingCheck(
        index = index,
        meetsPrerequisites = requiredUnmet.isEmpty() && optionsSatisfied,
        unmetPrerequisites = (requiredUnmet + optionsUnmet).map {
            UnmetPrerequisite(it.abilityScore, it.minimumScore, known.scoreOf(it.abilityScore))
        },
    )
}

private fun Map<String, Int>.scoreOf(ability: ApiReference): Int =
    this[ability.index] ?: throw IllegalStateException("Prerequisite names no known ability score: ${ability.index}")

private fun Map<String, Int>.meets(requirement: Requirement) =
    scoreOf(requirement.abilityScore) >= requirement.minimumScore
