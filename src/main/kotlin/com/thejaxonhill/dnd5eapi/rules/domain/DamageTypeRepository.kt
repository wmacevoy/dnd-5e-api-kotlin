package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface DamageTypeRepository {
    fun loadByIndex(index: Index): DamageType?
    fun loadAll(page: Page, example: DamageTypeExample? = null): PagedModel<DamageType>
}