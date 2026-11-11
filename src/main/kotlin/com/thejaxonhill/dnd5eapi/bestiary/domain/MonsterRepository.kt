package com.thejaxonhill.dnd5eapi.bestiary.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface MonsterRepository {
    fun loadByIndex(index: String): Monster?
    fun loadAll(page: Page, example: MonsterExample? = null): PagedModel<Monster>
}