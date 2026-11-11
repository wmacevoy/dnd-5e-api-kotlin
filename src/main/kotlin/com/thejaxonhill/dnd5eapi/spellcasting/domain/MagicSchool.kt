package com.thejaxonhill.dnd5eapi.spellcasting.domain


data class MagicSchool(
    val desc: String,
    val index: String,
    val name: String,
    val url: String,
)

data class MagicSchoolExample(val name: String? )