package com.thejaxonhill.dnd5eapi.spellcasting.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index


data class MagicSchool(
    val desc: String,
    val index: Index,
    val name: String,
    val url: String,
)

data class MagicSchoolExample(val name: String? )