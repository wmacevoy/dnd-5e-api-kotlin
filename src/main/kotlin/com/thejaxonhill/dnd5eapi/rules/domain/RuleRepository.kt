package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface RuleRepository {
    fun loadByIndex(index: Index): Rule?
    fun loadAll(page: Page, example: RuleExample? = null): PagedModel<Rule>
}