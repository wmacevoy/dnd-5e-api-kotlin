package com.thejaxonhill.dnd5eapi.shared

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class IndexTest {
    @Test
    fun `accepts upstream's forms, including runs of hyphens`() {
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
    fun `an upstream document whose index breaks the rule is refused`() {
        assertFailsWith<IllegalArgumentException> { Index.of("Acid Arrow") }
    }
}
