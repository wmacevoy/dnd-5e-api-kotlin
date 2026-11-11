package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface DamageTypeRepository {
    fun loadByIndex(index: String): DamageType?
    fun loadAll(page: Page, example: DamageTypeExample? = null): PagedModel<DamageType>
}