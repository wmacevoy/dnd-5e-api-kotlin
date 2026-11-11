package com.thejaxonhill.dnd5eapi.progression.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface LevelRepository {
    fun loadByIndex(index: String): Level?
    fun loadAll(page: Page, example: LevelExample? = null): PagedModel<Level>
}