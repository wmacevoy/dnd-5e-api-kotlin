package com.thejaxonhill.dnd5eapi.integration.level

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thejaxonhill.dnd5eapi.integration.shared.ControllerTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private val om = jacksonObjectMapper()

class LevelControllerTest : ControllerTest("/api/levels", "barbarian-6") {
    // REQ1/API-3: an item carries the fields upstream stores, spell slots included, in the API's camelCase.
    @Test
    fun `when get a spellcasting level then returns its spell slots`() {
        val spellcasting = mvc.perform(get("/api/levels/wizard-1"))
            .andReturn().response.contentAsString.let { om.readTree(it) }.path("spellcasting")

        assertEquals(2, assertNotNull(spellcasting.get("spellSlotsLevel1")).asInt())
        assertEquals(0, assertNotNull(spellcasting.get("spellSlotsLevel9")).asInt())
    }
}
