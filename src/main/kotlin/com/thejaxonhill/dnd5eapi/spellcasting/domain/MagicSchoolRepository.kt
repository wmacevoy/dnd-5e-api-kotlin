package com.thejaxonhill.dnd5eapi.spellcasting.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel

interface MagicSchoolRepository {
    fun loadByIndex(index: Index): MagicSchool?
    fun loadAll(page: Page, example: MagicSchoolExample? = null): PagedModel<MagicSchool>
}