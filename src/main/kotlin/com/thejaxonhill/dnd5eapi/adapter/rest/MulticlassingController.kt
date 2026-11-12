package com.thejaxonhill.dnd5eapi.adapter.rest

import com.thejaxonhill.dnd5eapi.adapter.published.published
import com.thejaxonhill.dnd5eapi.rules.ABILITIES
import com.thejaxonhill.dnd5eapi.rules.AbilityScores
import com.thejaxonhill.dnd5eapi.rules.CharacterRules
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * EXPERIMENT/CHANGE-4 over REST: the six scores are query parameters, named by ability index (`str`,
 * `dex`, ...). Scores are read here, so a score that is not a whole number is refused in the same
 * words as an absent one; the rule itself is in `rules/`.
 */
@RestController
class MulticlassingController(private val rules: CharacterRules) {
    @GetMapping("/api/classes/{index}/multiclassing")
    fun multiclassing(@PathVariable index: String, @RequestParam parameters: Map<String, String>): Map<String, Any?> =
        published(rules.multiclassing(index, scoresFrom(parameters)))
}

private fun scoresFrom(parameters: Map<String, String>) = AbilityScores(
    ABILITIES.associateWith { ability ->
        parameters[ability]?.let { text ->
            text.toIntOrNull() ?: throw IllegalArgumentException("Ability score '$ability' must be a whole number, not '$text'")
        }
    },
)
