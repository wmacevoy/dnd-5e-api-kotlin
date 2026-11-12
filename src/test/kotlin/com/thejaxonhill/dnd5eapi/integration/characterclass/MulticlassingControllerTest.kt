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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val om = jacksonObjectMapper()

// EXPERIMENT/CHANGE-4 (no requirement version states it yet): six ability scores and a class say
// whether a character meets the class's multiclassing prerequisites, and which are unmet.
@SpringBootTest
@AutoConfigureMockMvc
class MulticlassingControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    // Scores not named in a test are high enough to meet any prerequisite.
    private fun check(index: String, str: String = "15", dex: String = "15", wis: String = "15") =
        mvc.perform(
            get("/api/classes/$index/multiclassing")
                .param("str", str).param("dex", dex).param("con", "15")
                .param("int", "15").param("wis", wis).param("cha", "15"),
        ).andReturn().response.let { it.status to om.readTree(it.contentAsString) }

    @Test
    fun `a class with an either-or prerequisite is met by one of its options`() {
        val (status, body) = check("fighter", str = "13", dex = "8")
        assertEquals(200, status)
        assertTrue(body.path("eligible").asBoolean())
        assertNull(body.get("unmetChoice"))
        assertEquals(0, body.path("unmetPrerequisites").size())
    }

    @Test
    fun `a failed either-or prerequisite lists every option as unmet`() {
        val (_, body) = check("fighter", str = "12", dex = "12")
        assertFalse(body.path("eligible").asBoolean())
        val choice = body.path("unmetChoice")
        assertEquals(1, choice.path("choose").asInt())
        assertEquals(listOf("str", "dex"), choice.path("options").map { it.path("abilityScore").path("index").asText() })
        assertEquals(listOf(13, 13), choice.path("options").map { it.path("minimumScore").asInt() })
    }

    @Test
    fun `an all-required prerequisite lists only the unmet ones`() {
        val (_, body) = check("monk", dex = "13", wis = "12")
        assertFalse(body.path("eligible").asBoolean())
        assertNull(body.get("unmetChoice"))
        val unmet = body.path("unmetPrerequisites")
        assertEquals(1, unmet.size())
        assertEquals("wis", unmet[0].path("abilityScore").path("index").asText())
        assertEquals(13, unmet[0].path("minimumScore").asInt())
    }

    @Test
    fun `a class with prerequisites all met is eligible`() {
        val (_, body) = check("paladin", str = "13", dex = "8")
        assertTrue(body.path("eligible").asBoolean())
    }

    @Test
    fun `an unknown class is 404`() {
        check("not-found").let { (status, _) -> assertEquals(404, status) }
    }

    @Test
    fun `a missing or unreadable score is 400, not a default`() {
        mvc.perform(get("/api/classes/fighter/multiclassing").param("str", "13"))
            .andExpect(status().isBadRequest)
        mvc.perform(get("/api/classes/fighter/multiclassing").param("str", "thirteen").param("dex", "13")
            .param("con", "13").param("int", "13").param("wis", "13").param("cha", "13"))
            .andExpect(status().isBadRequest)
        mvc.perform(get("/api/classes/fighter/multiclassing").param("str", "31").param("dex", "13")
            .param("con", "13").param("int", "13").param("wis", "13").param("cha", "13"))
            .andExpect(status().isBadRequest)
    }
}
