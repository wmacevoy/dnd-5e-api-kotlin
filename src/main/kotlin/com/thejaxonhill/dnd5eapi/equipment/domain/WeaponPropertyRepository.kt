package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface WeaponPropertyRepository {
    fun loadByIndex(index: String): WeaponProperty?
    fun loadAll(page: Page, example: WeaponPropertyExample? = null): PagedModel<WeaponProperty>
}