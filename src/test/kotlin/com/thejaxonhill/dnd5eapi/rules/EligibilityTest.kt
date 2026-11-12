package com.thejaxonhill.dnd5eapi.rules

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// EXPERIMENT/CHANGE-4 (no requirement version states it yet): the rule, with no database or server.
class EligibilityTest {
    private val strength13 = ScoreMinimum("str", "STR", 13)
    private val dexterity13 = ScoreMinimum("dex", "DEX", 13)
    private val wisdom13 = ScoreMinimum("wis", "WIS", 13)

    private fun scores(vararg overrides: Pair<String, Int>): AbilityScores {
        val values = ABILITIES.associateWith { 10 as Int? }.toMutableMap()
        for ((ability, score) in overrides) values[ability] = score
        return AbilityScores(values)
    }

    @Test
    fun `an all-required class is eligible only when every prerequisite is met`() {
        val rule = Multiclassing(required = listOf(dexterity13, wisdom13), choice = null)
        assertTrue(evaluate(rule, scores("dex" to 13, "wis" to 13)).eligible)
        val unmet = evaluate(rule, scores("dex" to 12, "wis" to 13))
        assertFalse(unmet.eligible)
        assertEquals(listOf(dexterity13), unmet.unmetRequired)
    }

    @Test
    fun `a score exactly at the minimum meets it, and one below does not`() {
        val rule = Multiclassing(required = listOf(strength13), choice = null)
        assertTrue(evaluate(rule, scores("str" to 13)).eligible)
        assertFalse(evaluate(rule, scores("str" to 12)).eligible)
    }

    @Test
    fun `a choice is met by choose options, and unmet lists every option`() {
        val rule = Multiclassing(required = emptyList(), choice = Choice(1, listOf(strength13, dexterity13)))
        assertTrue(evaluate(rule, scores("dex" to 13)).eligible)
        val unmet = evaluate(rule, scores())
        assertFalse(unmet.eligible)
        assertTrue(unmet.unmetRequired.isEmpty())
        assertEquals(listOf(strength13, dexterity13), unmet.unmetChoice?.options)
    }

    // The input may be absent, late, wrong, or lying (REQ1/API-4 style rules, applied to scores).
    @Test
    fun `a score set with a missing ability, or a score out of range, is refused`() {
        assertFailsWith<IllegalArgumentException> { AbilityScores(mapOf("str" to 10)) }
        assertFailsWith<IllegalArgumentException> { scores("str" to 0) }
        assertFailsWith<IllegalArgumentException> { scores("str" to 31) }
    }

    // The Compendium's entry is upstream's data, so a choice of anything but ability scores is refused, not ignored.
    @Test
    fun `a prerequisite of a kind the rule does not know is refused, not ignored`() {
        val entry = mapOf(
            "multiClassing" to mapOf(
                "prerequisiteOptions" to mapOf(
                    "choose" to 1,
                    "from" to mapOf("options" to listOf(mapOf("optionType" to "reference"))),
                ),
            ),
        )
        assertFailsWith<IllegalStateException> { multiclassingOf("test", entry) }
    }
}
