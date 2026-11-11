package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference
import com.thejaxonhill.dnd5eapi.shared.domain.model.Choice

data class Race(
    val abilityBonusOptions: Choice? = null,
    val abilityBonuses: List<AbilityBonus>,
    val age: String,
    val alignment: String,
    val index: String,
    val languageDesc: String,
    val languageOptions: Choice? = null,
    val languages: List<ApiReference>,
    val name: String,
    val size: String,
    val sizeDescription: String,
    val speed: Long,
    val startingProficiencies: List<ApiReference>? = null,
    val startingProficiencyOptions: Choice? = null,
    val subraces: List<ApiReference>? = null,
    val traits: List<ApiReference>? = null,
    val url: String,
)

data class RaceExample(val name: String?)
data class AbilityBonus(
    val abilityScore: ApiReference,
    val bonus: Long,
)