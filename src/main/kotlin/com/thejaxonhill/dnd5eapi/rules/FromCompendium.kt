package com.thejaxonhill.dnd5eapi.rules

import com.thejaxonhill.dnd5eapi.compendium.Entry

/**
 * Reads a class's multiclassing rules as the Compendium spells them. This is the anticorruption layer
 * between the Compendium and Character Rules: upstream's shape stops here. An option this rule does not
 * evaluate is refused, not skipped, because skipping it would let a character in that the rules keep out.
 */
fun multiclassingOf(index: String, entry: Entry): Multiclassing {
    val rules = entry["multiClassing"] as? Map<*, *> ?: throw IllegalStateException("'$index' has no multiclassing rules")
    val required = (rules["prerequisites"] as? List<*>).orEmpty().map { scoreMinimum(it as Map<*, *>) }
    val choice = (rules["prerequisiteOptions"] as? Map<*, *>)?.let { options ->
        val from = options["from"] as Map<*, *>
        val choices = (from["options"] as List<*>).map { option ->
            option as Map<*, *>
            val type = option["optionType"]
            if (type != "score_prerequisite") {
                throw IllegalStateException("'$index' has a prerequisite option of type '$type', which this API does not evaluate")
            }
            scoreMinimum(option)
        }
        Choice(number(options["choose"]), choices)
    }
    return Multiclassing(required, choice)
}

private fun scoreMinimum(prerequisite: Map<*, *>): ScoreMinimum {
    val ability = prerequisite["abilityScore"] as Map<*, *>
    return ScoreMinimum(ability["index"] as String, ability["name"] as String, number(prerequisite["minimumScore"]))
}

private fun number(value: Any?) = (value as Number).toInt()
