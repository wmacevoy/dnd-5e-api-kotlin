package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface SubraceRepository {
    fun loadByIndex(index: String): Subrace?
    fun loadAll(page: Page, example: SubraceExample? = null): PagedModel<Subrace>
}