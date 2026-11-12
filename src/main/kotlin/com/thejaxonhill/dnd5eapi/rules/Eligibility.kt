package com.thejaxonhill.dnd5eapi.rules

/** The six abilities, by the index the Compendium gives them. */
val ABILITIES = listOf("str", "dex", "con", "int", "wis", "cha")

/**
 * A character's six ability scores, each a whole number from 1 to 30. Built only with all six: an
 * absent or impossible score is refused here, never given a default (REQ2/SHAPE-5). The 1-to-30 range
 * is this API's choice, not a stated requirement.
 */
class AbilityScores(scores: Map<String, Int?>) {
    private val values: Map<String, Int> = ABILITIES.associateWith { ability ->
        val score = scores[ability] ?: throw IllegalArgumentException("Ability score '$ability' is required")
        require(score in 1..30) { "Ability score '$ability' must be from 1 to 30, not $score" }
        score
    }

    fun score(ability: String): Int = values.getValue(ability)
}

/** One score a class asks for: at least [minimum] in [ability]. [name] is the SRD's abbreviation, such as STR. */
data class ScoreMinimum(val ability: String, val name: String, val minimum: Int) {
    fun metBy(scores: AbilityScores) = scores.score(ability) >= minimum
}

/** A choice: at least [choose] of [options] must be met. */
data class Choice(val choose: Int, val options: List<ScoreMinimum>)

/** What a class asks of a character before it can multiclass into it: all of [required], and a [choice] if any. */
class Multiclassing(val required: List<ScoreMinimum>, val choice: Choice?)

/** Which requirements a character fails. [unmetChoice] holds every option when the choice is not met. */
class Eligibility(val unmetRequired: List<ScoreMinimum>, val unmetChoice: Choice?) {
    val eligible: Boolean get() = unmetRequired.isEmpty() && unmetChoice == null
}

/** The rule itself, pure: the same rules and scores always give the same answer (REQ2/SHAPE-5). */
fun evaluate(rule: Multiclassing, scores: AbilityScores): Eligibility {
    val unmetRequired = rule.required.filterNot { it.metBy(scores) }
    val unmetChoice = rule.choice?.takeUnless { choice -> choice.options.count { it.metBy(scores) } >= choice.choose }
    return Eligibility(unmetRequired, unmetChoice)
}
