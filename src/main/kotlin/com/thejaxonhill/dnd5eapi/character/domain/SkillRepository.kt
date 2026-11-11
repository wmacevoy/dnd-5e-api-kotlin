package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface SkillRepository {
    fun loadByIndex(index: Index): Skill?
    fun loadAll(page: Page, example: SkillExample? = null): PagedModel<Skill>
}