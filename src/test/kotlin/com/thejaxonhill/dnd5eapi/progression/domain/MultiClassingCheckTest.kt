package com.thejaxonhill.dnd5eapi.progression.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference
import com.thejaxonhill.dnd5eapi.shared.domain.model.Choice
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Option
import com.thejaxonhill.dnd5eapi.shared.domain.model.OptionsArrayOptionSet
import com.thejaxonhill.dnd5eapi.shared.domain.model.ScorePrerequisiteOption
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// EXPERIMENT.md change 4 (no requirement ID yet; see the report). Plain values, no Spring, no database.
class MultiClassingCheckTest {
    private val everyScoreAt13 = mapOf("str" to 13, "dex" to 13, "con" to 13, "int" to 13, "wis" to 13, "cha" to 13)

    private fun scoreRef(index: String) = ApiReference(index, index.uppercase(), "/api/2014/ability-scores/$index")

    private fun required(index: String, minimum: Int) = MultiClassingPrerequisite(scoreRef(index), minimum)

    private fun option(index: String, minimum: Int): Option = ScorePrerequisiteOption(scoreRef(index), minimum)

    private fun choose(count: Int, vararg options: Option) = Choice(
        choose = count,
        type = "ability-scores",
        from = OptionsArrayOptionSet(options.toList()),
    )

    private fun classWith(multiClassing: MultiClassing?) = CharacterClass(index = Index.of("test-class"), multiClassing = multiClassing)

    @Test
    fun `a score equal to the minimum meets a required prerequisite`() {
        val check = classWith(MultiClassing(prerequisites = listOf(required("str", 13))))
            .checkMultiClassing(everyScoreAt13)

        assertTrue(check.meetsPrerequisites)
        assertTrue(check.unmetPrerequisites.isEmpty())
    }

    @Test
    fun `a required score below the minimum is unmet, with the score the character has`() {
        val check = classWith(MultiClassing(prerequisites = listOf(required("str", 13))))
            .checkMultiClassing(everyScoreAt13 + ("str" to 12))

        assertEquals(false, check.meetsPrerequisites)
        val unmet = check.unmetPrerequisites.single()
        assertEquals("str", unmet.abilityScore.index)
        assertEquals(13, unmet.minimumScore)
        assertEquals(12, unmet.score)
    }

    @Test
    fun `every required prerequisite must be met`() {
        val monk = classWith(MultiClassing(prerequisites = listOf(required("dex", 13), required("wis", 13))))

        val check = monk.checkMultiClassing(everyScoreAt13 + ("wis" to 8))

        assertEquals(false, check.meetsPrerequisites)
        assertEquals(listOf("wis"), check.unmetPrerequisites.map { it.abilityScore.index })
    }

    @Test
    fun `a choice is met by any one of its options`() {
        val fighter = classWith(
            MultiClassing(prerequisiteOptions = choose(1, option("str", 13), option("dex", 13)))
        )

        val check = fighter.checkMultiClassing(everyScoreAt13 + ("str" to 8))

        assertTrue(check.meetsPrerequisites)
        assertTrue(check.unmetPrerequisites.isEmpty())
    }

    @Test
    fun `when no option of a choice is met, every option is unmet`() {
        val fighter = classWith(
            MultiClassing(prerequisiteOptions = choose(1, option("str", 13), option("dex", 13)))
        )

        val check = fighter.checkMultiClassing(everyScoreAt13 + ("str" to 8) + ("dex" to 8))

        assertEquals(false, check.meetsPrerequisites)
        assertEquals(listOf("str", "dex"), check.unmetPrerequisites.map { it.abilityScore.index })
    }

    @Test
    fun `a required prerequisite and a choice both count, and only the unmet ones are listed`() {
        val both = classWith(
            MultiClassing(
                prerequisites = listOf(required("cha", 13)),
                prerequisiteOptions = choose(1, option("str", 13), option("dex", 13)),
            )
        )

        val check = both.checkMultiClassing(everyScoreAt13 + ("cha" to 8) + ("str" to 13))

        assertEquals(false, check.meetsPrerequisites)
        assertEquals(listOf("cha"), check.unmetPrerequisites.map { it.abilityScore.index })
    }

    @Test
    fun `a class with no prerequisites is met by any scores`() {
        val check = classWith(MultiClassing()).checkMultiClassing(everyScoreAt13 + ("str" to 1))

        assertTrue(check.meetsPrerequisites)
    }

    // REQ1/API-4 style refusal: a score that is absent, or outside 1 to 30 (an invented bound), is an error.
    @Test
    fun `a missing score is refused, not assumed`() {
        val withoutCha = everyScoreAt13 - "cha"

        assertFailsWith<IllegalArgumentException> {
            classWith(MultiClassing()).checkMultiClassing(withoutCha)
        }
    }

    @Test
    fun `a score outside 1 to 30 is refused`() {
        for (bad in listOf(0, 31)) {
            assertFailsWith<IllegalArgumentException> {
                classWith(MultiClassing()).checkMultiClassing(everyScoreAt13 + ("str" to bad))
            }
        }
    }

    @Test
    fun `a class without multiclassing data is refused`() {
        assertFailsWith<IllegalStateException> {
            classWith(null).checkMultiClassing(everyScoreAt13)
        }
    }
}
