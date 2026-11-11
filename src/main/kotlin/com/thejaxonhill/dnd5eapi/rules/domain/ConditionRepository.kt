package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface ConditionRepository {
    fun loadByIndex(index: String): Condition?
    fun loadAll(page: Page, example: ConditionExample? = null): PagedModel<Condition>
}