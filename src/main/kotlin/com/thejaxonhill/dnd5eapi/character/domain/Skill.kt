package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference

data class Skill(
    val abilityScore: ApiReference,
    val desc: List<String>,
    val index: Index,
    val name: String,
    val url: String,
)

data class SkillExample(val name: String?)
