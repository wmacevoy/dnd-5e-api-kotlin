package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface RuleSectionRepository {
    fun loadByIndex(index: Index): RuleSection?
    fun loadAll(page: Page, example: RuleSectionExample? = null): PagedModel<RuleSection>
}