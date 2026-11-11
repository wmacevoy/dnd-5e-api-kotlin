package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface RaceRepository {
    fun loadByIndex(index: String): Race?
    fun loadAll(page: Page, example: RaceExample? = null): PagedModel<Race>
}