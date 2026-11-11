package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface FeatRepository {
    fun loadByIndex(index: Index): Feat?
    fun loadAll(page: Page, example: FeatExample? = null): PagedModel<Feat>
}