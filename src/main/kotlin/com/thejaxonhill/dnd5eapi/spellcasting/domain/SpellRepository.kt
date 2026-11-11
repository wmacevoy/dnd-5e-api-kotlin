package com.thejaxonhill.dnd5eapi.spellcasting.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface SpellRepository {
    fun loadByIndex(index: Index): Spell?
    fun loadAll(page: Page, example: SpellExample? = null): PagedModel<Spell>
}