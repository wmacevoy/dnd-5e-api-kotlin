package com.thejaxonhill.dnd5eapi.integration.level

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thejaxonhill.dnd5eapi.integration.shared.ControllerTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val om = jacksonObjectMapper()

class LevelControllerTest : ControllerTest("/api/levels", "barbarian-6") {
    // REQ3/DAT-1: upstream's spell_slots_level_N is read, and served under the same camelCase name as its other fields.
    @Test
    fun `when get caster level then returns its spell slots`() {
        val spellcasting = mvc.perform(get("/api/levels/wizard-1"))
            .andReturn().response.contentAsString.let { om.readTree(it) }.path("spellcasting")

        assertTrue(spellcasting.has("spellSlotsLevel1"))
        assertEquals(2, spellcasting.get("spellSlotsLevel1").asInt())
        assertTrue(spellcasting.has("spellSlotsLevel9"))
    }
}