package com.thejaxonhill.dnd5eapi.equipment.application.dto

import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponMasteryProperty

data class WeaponMasteryPropertyView(
    val description: String,
    val index: String,
    val name: String,
    val url: String,
)

fun WeaponMasteryProperty.toView() = WeaponMasteryPropertyView(
    description = description,
    index = index.value,
    name = name,
    url = url,
)
