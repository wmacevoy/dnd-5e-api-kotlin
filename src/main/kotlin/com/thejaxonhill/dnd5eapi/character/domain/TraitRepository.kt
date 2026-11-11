package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface TraitRepository {
    fun loadByIndex(index: Index): Trait?
    fun loadAll(page: Page, example: TraitExample? = null): PagedModel<Trait>
}