package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface BackgroundRepository {
    fun loadByIndex(index: String): Background?
    fun loadAll(page: Page, example: BackgroundExample? = null): PagedModel<Background>
}