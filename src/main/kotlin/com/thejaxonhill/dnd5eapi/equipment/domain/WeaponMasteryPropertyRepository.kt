package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface WeaponMasteryPropertyRepository {
    fun loadByIndex(index: Index): WeaponMasteryProperty?
    fun loadAll(page: Page, example: WeaponMasteryPropertyExample? = null): PagedModel<WeaponMasteryProperty>
}