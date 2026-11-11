package com.thejaxonhill.dnd5eapi.adapter

import com.thejaxonhill.dnd5eapi.compendium.Compendium
import com.thejaxonhill.dnd5eapi.compendium.CompendiumQueries
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Builds the Compendium's query service here, so the Compendium itself imports no framework (REQ2/SHAPE-6). */
@Configuration
class Wiring {
    @Bean
    fun compendiumQueries(compendium: Compendium) = CompendiumQueries(compendium)
}
