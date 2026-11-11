package com.thejaxonhill.dnd5eapi.integration.proficiency

import com.thejaxonhill.dnd5eapi.integration.shared.ControllerTest

// REQ1/API-1: proficiencies are served; a wrong collection name makes these fail.
class ProficiencyControllerTest : ControllerTest("/api/proficiencies", "heavy-armor")