package com.thejaxonhill.dnd5eapi.integration.level

import com.thejaxonhill.dnd5eapi.integration.shared.ControllerTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LevelControllerTest : ControllerTest("/api/levels", "barbarian-6") {
    // REQ1/API-3: an item carries its class-specific fields, named in camelCase like the others.
    @Test
    fun `when get bard level 18 then returns magical secrets fields`() {
        mvc.perform(get("/api/levels/bard-18"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.classSpecific.magicalSecretsMax5").value(2))
            .andExpect(jsonPath("$.classSpecific.magicalSecretsMax7").value(2))
            .andExpect(jsonPath("$.classSpecific.magicalSecretsMax9").value(2))
    }

    // REQ1/API-3: the warlock's mystic arcanum fields are returned the same way.
    @Test
    fun `when get warlock level 17 then returns mystic arcanum fields`() {
        mvc.perform(get("/api/levels/warlock-17"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.classSpecific.mysticArcanumLevel6").value(1))
            .andExpect(jsonPath("$.classSpecific.mysticArcanumLevel7").value(1))
            .andExpect(jsonPath("$.classSpecific.mysticArcanumLevel8").value(1))
            .andExpect(jsonPath("$.classSpecific.mysticArcanumLevel9").value(1))
    }
}
