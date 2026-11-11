package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface AbilityScoreRepository {
    fun loadByIndex(index: Index): AbilityScore?
    fun loadAll(page: Page, example: AbilityScoreExample? = null): PagedModel<AbilityScore>
}