package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.LanguageRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadLanguageByIndex(private val languageRepository: LanguageRepository) {
    fun load(index: String) = languageRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}