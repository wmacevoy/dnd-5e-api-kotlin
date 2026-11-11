package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface EquipmentRepository {
    fun loadByIndex(index: Index): Equipment?
    fun loadAll(page: Page, example: EquipmentExample? = null): PagedModel<Equipment>
    fun loadAll(page: Page, query: String? = null): PagedModel<Equipment>
}