package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface ProficiencyRepository {
    fun loadByIndex(index: Index): Proficiency?
    fun loadAll(page: Page, example: ProficiencyExample? = null): PagedModel<Proficiency>
}