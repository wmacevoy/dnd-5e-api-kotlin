package com.thejaxonhill.dnd5eapi.integration.level

import com.thejaxonhill.dnd5eapi.integration.shared.ControllerTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

class LevelControllerTest : ControllerTest("/api/levels", "barbarian-6") {
    // REQ3/DAT-1: upstream's `magical_secrets_max_5` is read, so the field reaches the output.
    @Test
    fun `when get bard level then returns magical secrets`() {
        mvc.perform(get("/api/levels/bard-10"))
            .andExpect(jsonPath("$.classSpecific.magicalSecretsMax5").value(2))
    }

    // REQ3/DAT-1: upstream's `mystic_arcanum_level_6` is read, so the field reaches the output.
    @Test
    fun `when get warlock level then returns mystic arcanum`() {
        mvc.perform(get("/api/levels/warlock-11"))
            .andExpect(jsonPath("$.classSpecific.mysticArcanumLevel6").value(1))
    }
}
