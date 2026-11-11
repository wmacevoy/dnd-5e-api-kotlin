package com.thejaxonhill.dnd5eapi.integration.level

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thejaxonhill.dnd5eapi.integration.shared.ControllerTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import kotlin.test.assertEquals

private val om = jacksonObjectMapper()

class LevelControllerTest : ControllerTest("/api/levels", "barbarian-6") {
    // REQ2/SHAPE-3: a spellcasting level carries its slots under camelCase names, as its other fields do.
    @Test
    fun `REQ2 SHAPE-3 when get spellcasting level then returns its spell slots`() {
        val spellcasting = mvc.perform(get("/api/levels/wizard-1"))
            .andReturn().response.contentAsString.let { om.readTree(it) }.path("spellcasting")

        assertEquals(2, spellcasting.path("spellSlotsLevel1").asInt())
    }
}