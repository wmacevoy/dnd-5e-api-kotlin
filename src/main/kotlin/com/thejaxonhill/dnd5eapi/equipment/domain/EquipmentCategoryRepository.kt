package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface EquipmentCategoryRepository {
    fun loadByIndex(index: Index): EquipmentCategory?
    fun loadAll(page: Page, example: EquipmentCategoryExample? = null): PagedModel<EquipmentCategory>
}
