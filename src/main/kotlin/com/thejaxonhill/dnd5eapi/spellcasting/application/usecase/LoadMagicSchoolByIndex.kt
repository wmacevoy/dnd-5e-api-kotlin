package com.thejaxonhill.dnd5eapi.spellcasting.application.usecase

import com.thejaxonhill.dnd5eapi.spellcasting.application.dto.toView
import com.thejaxonhill.dnd5eapi.spellcasting.domain.MagicSchoolRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadMagicSchoolByIndex(private val magicSchoolRepository: MagicSchoolRepository) {
    fun load(index: String) = magicSchoolRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}