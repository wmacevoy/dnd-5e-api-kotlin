package com.thejaxonhill.dnd5eapi

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.assertFalse

// The multiclassing rule, with no database (EXPERIMENT.md change 4; no versioned requirement ID yet).
// Class documents are upstream's, in its snake_case, as the store returns them.

private fun prerequisite(ability: String, minimum: Int) =
    mapOf("ability_score" to mapOf("index" to ability), "minimum_score" to minimum)

private fun option(ability: String, minimum: Int) =
    prerequisite(ability, minimum) + ("option_type" to "score_prerequisite")

private fun allOf(vararg prerequisites: Map<String, Any?>) =
    mapOf("multi_classing" to mapOf("prerequisites" to prerequisites.toList()))

private fun chooseOne(vararg options: Map<String, Any?>) = mapOf(
    "multi_classing" to mapOf(
        "prerequisite_options" to mapOf(
            "type" to "ability-scores",
            "choose" to 1,
            "from" to mapOf("option_set_type" to "options_array", "options" to options.toList()),
        ),
    ),
)

private fun scoresOf(
    strength: Int = 10, dexterity: Int = 10, constitution: Int = 10,
    intelligence: Int = 10, wisdom: Int = 10, charisma: Int = 10,
) = mapOf(
    "strength" to strength, "dexterity" to dexterity, "constitution" to constitution,
    "intelligence" to intelligence, "wisdom" to wisdom, "charisma" to charisma,
)

class EligibilityTest {
    private val monk = allOf(prerequisite("dex", 13), prerequisite("wis", 13))
    private val fighter = chooseOne(option("str", 13), option("dex", 13))

    @Test
    fun `all-of - eligible when every stated prerequisite is met`() {
        val result = eligibility(monk, scoresOf(dexterity = 13, wisdom = 13))

        assertTrue(result.eligible)
        assertEquals(2, result.needed)
        assertTrue(result.unmet.isEmpty())
    }

    @Test
    fun `all-of - lists the unmet prerequisite with its minimum and the score given`() {
        val result = eligibility(monk, scoresOf(dexterity = 14, wisdom = 12))

        assertFalse(result.eligible)
        assertEquals(listOf("wisdom"), result.unmet.map { it.ability })
        assertEquals(13, result.unmet.single().minimumScore)
        assertEquals(12, result.unmet.single().score)
    }

    @Test
    fun `choice - eligible when enough options are met, and lists those not met`() {
        val result = eligibility(fighter, scoresOf(strength = 8, dexterity = 14))

        assertTrue(result.eligible)
        assertEquals(1, result.needed)
        assertEquals(listOf("strength"), result.unmet.map { it.ability })
    }

    @Test
    fun `choice - not eligible when no option is met, and lists every option`() {
        val result = eligibility(fighter, scoresOf(strength = 12, dexterity = 12))

        assertFalse(result.eligible)
        assertEquals(listOf("strength", "dexterity"), result.unmet.map { it.ability })
    }

    @Test
    fun `a class that states no prerequisites is not found`() {
        assertFailsWith<NoSuchElementException> {
            eligibility(mapOf("index" to "x"), scoresOf())
        }
    }

    @Test
    fun `an ability score upstream does not name is refused`() {
        assertFailsWith<IllegalStateException> {
            eligibility(allOf(prerequisite("luck", 13)), scoresOf())
        }
    }

    @Test
    fun `scores - each of the six is required`() {
        val missing = scoresOf() - "wisdom"

        assertFailsWith<IllegalArgumentException> { scores(missing) }
    }

    @Test
    fun `scores - a score must be a whole number from 1 to 30`() {
        for (bad in listOf("0", "31", "abc", "13.5", "")) {
            assertFailsWith<IllegalArgumentException>("score '$bad'") {
                scores(scoresOf().mapValues { bad })
            }
        }
    }

    @Test
    fun `scores - text numbers from a request are read`() {
        assertEquals(13, scores(scoresOf().mapValues { "13" })["strength"])
    }
}
