package com.thejaxonhill.dnd5eapi

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

// REQ2/SHAPE-3: an item is upstream's document in camelCase, departing only where the row says so.
// The level row used to drop these fields, so this fails without the fix.
class LevelClassSpecificTest {
    private val levels = RESOURCES.single { it.path == "levels" }

    @Test
    fun `REQ2-SHAPE-3 a bard level keeps its magical secrets fields`() {
        val document = mapOf(
            "index" to "bard-10",
            "class_specific" to mapOf(
                "magical_secrets_max_5" to 2,
                "magical_secrets_max_7" to 0,
                "magical_secrets_max_9" to 0,
            ),
        )

        val classSpecific = item(levels, document)["classSpecific"] as Map<*, *>

        assertEquals(2, classSpecific["magicalSecretsMax5"])
        assertEquals(0, classSpecific["magicalSecretsMax7"])
        assertEquals(0, classSpecific["magicalSecretsMax9"])
    }

    @Test
    fun `REQ2-SHAPE-3 a warlock level keeps its mystic arcanum fields, on MCP too`() {
        val document = mapOf(
            "index" to "warlock-11",
            "class_specific" to mapOf(
                "invocations_known" to 5,
                "mystic_arcanum_level_6" to 1,
                "mystic_arcanum_level_7" to 1,
                "mystic_arcanum_level_8" to 1,
                "mystic_arcanum_level_9" to 1,
            ),
        )

        val rest = item(levels, document)["classSpecific"] as Map<*, *>
        val mcp = mcpItem(levels, document)["classSpecific"] as Map<*, *>

        for (level in 6..9) {
            assertEquals(1, rest["mysticArcanumLevel$level"])
            assertEquals(1, mcp["mysticArcanumLevel$level"])
        }
    }
}
