package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference

data class EquipmentCategory(
    val equipment: List<ApiReference>,
    val index: Index,
    val name: String,
    val url: String,
)

data class EquipmentCategoryExample(val name: String?)
