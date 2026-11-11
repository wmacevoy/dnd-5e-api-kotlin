package com.thejaxonhill.dnd5eapi.progression.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface CharacterClassRepository {
    fun loadByIndex(index: Index): CharacterClass?
    fun loadAll(page: Page, example: CharacterClassExample? = null): PagedModel<CharacterClass>
}