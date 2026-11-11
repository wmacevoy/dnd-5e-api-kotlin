package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface LanguageRepository {
    fun loadByIndex(index: Index): Language?
    fun loadAll(page: Page, example: LanguageExample? = null): PagedModel<Language>
}