package com.thejaxonhill.dnd5eapi.equipment.application.dto

import com.thejaxonhill.dnd5eapi.equipment.domain.MagicItem
import com.thejaxonhill.dnd5eapi.equipment.domain.Rarity
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference

data class MagicItemView(
    val desc: List<String>,
    val equipmentCategory: ApiReference,
    val image: String? = null,
    val index: String,
    val name: String,
    val rarity: Rarity,
    val url: String,
    val variants: List<ApiReference>,
    val variant: Boolean,
)

fun MagicItem.toView() = MagicItemView(
    desc = desc,
    equipmentCategory = equipmentCategory,
    image = image,
    index = index.value,
    name = name,
    rarity = rarity,
    url = url,
    variants = variants,
    variant = variant,
)
