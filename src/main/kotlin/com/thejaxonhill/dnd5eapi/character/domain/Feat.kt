package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference

data class Feat(
    val index: Index,
    val name: String,
    val prerequisites: List<FeatPrerequisite>,
    val desc: List<String>,
    val url: String,
)

data class FeatExample(val name: String?)
data class FeatPrerequisite(
    val abilityScore: ApiReference,
    val minimumScore: Int,
)