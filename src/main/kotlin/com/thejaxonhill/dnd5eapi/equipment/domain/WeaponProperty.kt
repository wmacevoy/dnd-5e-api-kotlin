package com.thejaxonhill.dnd5eapi.equipment.domain


data class WeaponProperty(
    val desc: List<String>,
    val index: String,
    val name: String,
    val url: String,
)

data class WeaponPropertyExample(val name: String?)
