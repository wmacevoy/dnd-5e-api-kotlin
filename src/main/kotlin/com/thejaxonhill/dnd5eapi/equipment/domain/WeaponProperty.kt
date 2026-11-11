package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index


data class WeaponProperty(
    val desc: List<String>,
    val index: Index,
    val name: String,
    val url: String,
)

data class WeaponPropertyExample(val name: String?)
