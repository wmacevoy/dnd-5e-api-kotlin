package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference
import com.thejaxonhill.dnd5eapi.shared.domain.model.Choice

class Background(
    val index: Index,
    val name: String? = null,
    val startingProficiencies: List<ApiReference>? = null,
    val languageOptions: Choice? = null,
    val startingEquipment: List<StartingEquipment>? = null,
    val startingEquipmentOptions: List<Choice>? = null,
    val feature: Feature? = null,
    val personalityTraits: Choice? = null,
    val ideals: Choice? = null,
    val bonds: Choice? = null,
    val flaws: Choice? = null,
    val url: String? = null,
)

data class Feature(
    val name: String,
    val desc: List<String>
)

data class StartingEquipment(
    val equipment: ApiReference,
    val quantity: Int,
)

data class BackgroundExample(val name: String?)
