package com.thejaxonhill.dnd5eapi.spellcasting.application.usecase

import com.thejaxonhill.dnd5eapi.spellcasting.application.dto.toView
import com.thejaxonhill.dnd5eapi.spellcasting.domain.MagicSchoolExample
import com.thejaxonhill.dnd5eapi.spellcasting.domain.MagicSchoolRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadMagicSchools(private val magicSchoolRepository: MagicSchoolRepository) {
    fun load(page: Page, example: MagicSchoolExample?) =
        magicSchoolRepository.loadAll(page, example).map { it.toView() }
}