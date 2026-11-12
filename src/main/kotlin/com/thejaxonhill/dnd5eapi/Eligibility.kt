package com.thejaxonhill.dnd5eapi

// The multiclassing rule (EXPERIMENT.md change 4). Pure: a stored class and six scores in, an answer
// out, no I/O. Upstream states a class's prerequisites either as `prerequisites` (all required) or as
// `prerequisite_options` (choose some of the options), so the rule has one branch for each.

/** The six abilities: the name a request gives each score, and the index upstream stores it under. */
val ABILITIES = linkedMapOf(
    "strength" to "str",
    "dexterity" to "dex",
    "constitution" to "con",
    "intelligence" to "int",
    "wisdom" to "wis",
    "charisma" to "cha",
)

/** A prerequisite the scores do not meet. */
class Shortfall(val ability: String, val minimumScore: Int, val score: Int)

/**
 * [needed] of the [total] stated prerequisites must be met. [unmet] lists the ones that are not,
 * whether or not the class is eligible, so a choice shows every option it did not take.
 */
class Eligibility(val needed: Int, val total: Int, val unmet: List<Shortfall>) {
    val eligible: Boolean get() = total - unmet.size >= needed
}

private class Prerequisite(val ability: String, val minimumScore: Int)

/**
 * Checks [scores], keyed by [ABILITIES]' names, against a class document as the store returns it.
 * Fails with NoSuchElementException (404) when the class states no prerequisites, and with
 * IllegalStateException or a cast failure (400, the handler's rule for runtime exceptions) when a
 * prerequisite is in a form this rule does not read.
 */
fun eligibility(characterClass: Map<String, Any?>, scores: Map<String, Int>): Eligibility {
    val multiClassing = characterClass["multi_classing"] as? Map<*, *>
    val all = multiClassing?.get("prerequisites") as? List<*>
    val choice = multiClassing?.get("prerequisite_options") as? Map<*, *>
    val (needed, prerequisites) = when {
        all != null -> all.size to all.map { prerequisite(it as Map<*, *>) }
        choice != null -> {
            val options = ((choice["from"] as Map<*, *>)["options"] as List<*>).map { it as Map<*, *> }
            for (option in options) require(option["option_type"] == "score_prerequisite") {
                "unknown multiclassing option '${option["option_type"]}'"
            }
            (choice["choose"] as Number).toInt() to options.map { prerequisite(it) }
        }
        else -> throw NoSuchElementException()
    }
    val unmet = prerequisites
        .filter { (scores.getValue(it.ability)) < it.minimumScore }
        .map { Shortfall(it.ability, it.minimumScore, scores.getValue(it.ability)) }
    return Eligibility(needed, prerequisites.size, unmet)
}

private fun prerequisite(item: Map<*, *>): Prerequisite {
    val index = (item["ability_score"] as Map<*, *>)["index"]
    val ability = ABILITIES.entries.firstOrNull { it.value == index }?.key
        ?: throw IllegalStateException("unknown ability score '$index' in a multiclassing prerequisite")
    return Prerequisite(ability, (item["minimum_score"] as Number).toInt())
}

/**
 * The six scores from a request, which are text from a query or numbers from a tool call. Each is
 * required, and must be a whole number from 1 to 30; anything else is refused (400).
 */
fun scores(values: Map<String, Any?>): Map<String, Int> = ABILITIES.keys.associateWith { name ->
    val value = values[name] ?: throw IllegalArgumentException("$name is required")
    val score = value.toString().toIntOrNull()
    require(score != null && score in 1..30) { "$name must be a whole number from 1 to 30, not '$value'" }
    score
}

/** The answer as both REST and MCP return it. */
fun multiclassingView(index: String, result: Eligibility): Map<String, Any?> = linkedMapOf(
    "index" to index,
    "eligible" to result.eligible,
    "needed" to result.needed,
    "unmet" to result.unmet.map {
        mapOf("ability" to it.ability, "minimumScore" to it.minimumScore, "score" to it.score)
    },
)
