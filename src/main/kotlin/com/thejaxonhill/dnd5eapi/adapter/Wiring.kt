package com.thejaxonhill.dnd5eapi.adapter

import com.thejaxonhill.dnd5eapi.compendium.Compendium
import com.thejaxonhill.dnd5eapi.compendium.CompendiumQueries
import com.thejaxonhill.dnd5eapi.rules.CharacterRules
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Builds the query services here, so the Compendium and Character Rules import no framework (REQ2/SHAPE-6). */
@Configuration
class Wiring {
    @Bean
    fun compendiumQueries(compendium: Compendium) = CompendiumQueries(compendium)

    @Bean
    fun characterRules(queries: CompendiumQueries) = CharacterRules(queries)
}
