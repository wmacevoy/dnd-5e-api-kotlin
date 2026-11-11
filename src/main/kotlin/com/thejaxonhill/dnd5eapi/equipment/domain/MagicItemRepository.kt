package com.thejaxonhill.dnd5eapi.equipment.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface MagicItemRepository {
    fun loadByIndex(index: String): MagicItem?
    fun loadAll(page: Page, example: MagicItemExample? = null): PagedModel<MagicItem>
}