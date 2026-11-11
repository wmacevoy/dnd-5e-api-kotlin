package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface ConditionRepository {
    fun loadByIndex(index: Index): Condition?
    fun loadAll(page: Page, example: ConditionExample? = null): PagedModel<Condition>
}