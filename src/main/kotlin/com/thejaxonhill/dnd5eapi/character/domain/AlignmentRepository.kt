package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface AlignmentRepository {
    fun loadByIndex(index: String): Alignment?
    fun loadAll(page: Page, example: AlignmentExample? = null): PagedModel<Alignment>
}