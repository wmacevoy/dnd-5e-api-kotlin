package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index


data class WeaponMasteryProperty(
    val description: String,
    val index: Index,
    val name: String,
    val url: String,
)

data class WeaponMasteryPropertyExample(val name: String?)
