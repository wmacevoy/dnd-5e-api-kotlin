package com.thejaxonhill.dnd5eapi.compendium

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CompendiumValuesTest {
    @Test
    fun `an index accepts upstream's forms, including runs of hyphens`() {
        for (text in listOf("acid-arrow", "10-foot-pole", "dragon-ancestor-black---acid-damage", "x")) {
            assertEquals(text, Index.parse(text)?.value)
        }
    }

    // REQ1/API-4: text that cannot be an index finds nothing, so the API answers 404, not an error.
    @Test
    fun `text that cannot be an index parses to nothing`() {
        for (text in listOf("", "Acid-Arrow", "acid_arrow", "-acid", "acid-", "acid arrow", "../spells")) {
            assertNull(Index.parse(text), text)
        }
    }

    @Test
    fun `a page is numbered from zero and holds at least one entry`() {
        assertFailsWith<IllegalArgumentException> { PageRequest(-1, 20) }
        assertFailsWith<IllegalArgumentException> { PageRequest(0, 0) }
        assertEquals(1, PageRequest(1, 20).number)
    }
}
