package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference

class AbilityScore(
    val desc: List<String>? = null,
    val fullName: String? = null,
    val index: Index,
    val name: String? = null,
    val skills: List<ApiReference>? = null,
    val url: String? = null,
)

data class AbilityScoreExample(val name: String?)
