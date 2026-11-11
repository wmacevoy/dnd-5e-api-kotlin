package com.thejaxonhill.dnd5eapi.rules.domain


data class DamageType(
    val index: String,
    val name: String,
    val desc: List<String>,
    val url: String,
)

data class DamageTypeExample(val name: String?)
