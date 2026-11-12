package com.thejaxonhill.dnd5eapi.integration.characterclass

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals

private val om = jacksonObjectMapper()

// EXPERIMENT.md change 4 (no requirement ID yet; see the report). Runs against the seeded database.
@SpringBootTest
@AutoConfigureMockMvc
class MultiClassingCheckControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    private val everyScoreAt13 = "str=13&dex=13&con=13&int=13&wis=13&cha=13"

    private fun check(index: String, query: String) =
        mvc.perform(get("/api/classes/$index/multiclassing-check?$query"))

    @Test
    fun `a barbarian with strength 13 meets the prerequisite`() {
        val body = check("barbarian", everyScoreAt13).andExpect(status().isOk)
            .andReturn().response.contentAsString.let { om.readTree(it) }

        assertEquals(true, body.get("meetsPrerequisites").asBoolean())
        assertEquals(0, body.get("unmetPrerequisites").size())
    }

    @Test
    fun `a barbarian with strength 12 is unmet on strength, with both numbers`() {
        val body = check("barbarian", everyScoreAt13.replace("str=13", "str=12")).andExpect(status().isOk)
            .andReturn().response.contentAsString.let { om.readTree(it) }

        assertEquals(false, body.get("meetsPrerequisites").asBoolean())
        val unmet = body.get("unmetPrerequisites").single()
        assertEquals("str", unmet.get("abilityScore").get("index").asText())
        assertEquals(13, unmet.get("minimumScore").asInt())
        assertEquals(12, unmet.get("score").asInt())
    }

    @Test
    fun `a fighter is met by dexterity alone, because the choice is either score`() {
        val body = check("fighter", everyScoreAt13.replace("str=13", "str=8")).andExpect(status().isOk)
            .andReturn().response.contentAsString.let { om.readTree(it) }

        assertEquals(true, body.get("meetsPrerequisites").asBoolean())
    }

    @Test
    fun `a fighter with neither strength nor dexterity 13 is unmet on both options`() {
        val body = check("fighter", everyScoreAt13.replace("str=13", "str=8").replace("dex=13", "dex=8"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString.let { om.readTree(it) }

        assertEquals(false, body.get("meetsPrerequisites").asBoolean())
        assertEquals(listOf("str", "dex"), body.get("unmetPrerequisites").map { it.get("abilityScore").get("index").asText() })
    }

    // REQ1/API-4 style refusal: a missing or out-of-range score is a 400, never a 500 or an assumed value.
    @Test
    fun `a missing score is a bad request`() {
        check("barbarian", everyScoreAt13.replace("dex=13&", "")).andExpect(status().isBadRequest)
    }

    @Test
    fun `a score outside 1 to 30 is a bad request`() {
        check("barbarian", everyScoreAt13.replace("str=13", "str=31")).andExpect(status().isBadRequest)
    }

    @Test
    fun `an unknown class is not found`() {
        check("not-found", everyScoreAt13).andExpect(status().isNotFound)
    }
}
