package com.thejaxonhill.dnd5eapi.integration.characterclass

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// The multiclassing check over REST and MCP, against the seeded database (EXPERIMENT.md change 4;
// no versioned requirement ID yet). Both surfaces must answer the same way.
@SpringBootTest
@AutoConfigureMockMvc
class MulticlassingControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    @Qualifier("tools")
    lateinit var tools: ToolCallbackProvider

    private val json = jacksonObjectMapper()

    private val allTens = "strength=10&dexterity=10&constitution=10&intelligence=10&wisdom=10&charisma=10"

    @Test
    fun `when fighter scores allow dexterity then eligible, and strength is listed as unmet`() {
        val body = mvc.perform(get("/api/classes/fighter/multiclassing?strength=8&dexterity=14&constitution=10&intelligence=10&wisdom=10&charisma=10"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString.let { json.readTree(it) }

        assertTrue(body.get("eligible").asBoolean())
        assertEquals(1, body.get("needed").asInt())
        assertEquals("strength", body.get("unmet").single().get("ability").asText())
    }

    @Test
    fun `when monk lacks wisdom then not eligible, and wisdom is the unmet prerequisite`() {
        val body = mvc.perform(get("/api/classes/monk/multiclassing?strength=10&dexterity=14&constitution=10&intelligence=10&wisdom=12&charisma=10"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString.let { json.readTree(it) }

        assertFalse(body.get("eligible").asBoolean())
        val unmet = body.get("unmet").single()
        assertEquals("wisdom", unmet.get("ability").asText())
        assertEquals(13, unmet.get("minimumScore").asInt())
        assertEquals(12, unmet.get("score").asInt())
    }

    @Test
    fun `when a score is missing then returns 400`() {
        mvc.perform(get("/api/classes/monk/multiclassing?dexterity=14&wisdom=14"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `when class is unknown then returns 404`() {
        mvc.perform(get("/api/classes/not-found/multiclassing?$allTens")).andExpect(status().isNotFound)
    }

    @Test
    fun `when mcp tool is called then answers as REST does`() {
        val tool = tools.toolCallbacks.single { it.toolDefinition.name() == "checkMulticlassing" }
        val arguments = """{"index":"monk","strength":10,"dexterity":14,"constitution":10,"intelligence":10,"wisdom":12,"charisma":10}"""

        val result = json.readTree(tool.call(arguments))

        assertFalse(result.get("eligible").asBoolean())
        assertEquals("wisdom", result.get("unmet").single().get("ability").asText())
    }
}
