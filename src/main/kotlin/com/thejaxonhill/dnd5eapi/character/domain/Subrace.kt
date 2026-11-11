package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference
import com.thejaxonhill.dnd5eapi.shared.domain.model.Choice

data class Subrace(
    val abilityBonuses: List<AbilityBonus>,
    val desc: String,
    val index: String,
    val languages: List<ApiReference>? = null,
    val languageOptions: Choice? = null,
    val name: String,
    val race: ApiReference,
    val racialTraits: List<ApiReference>,
    val startingProficiencies: List<ApiReference>? = null,
    val url: String,
)

data class SubraceExample(val name: String?)

