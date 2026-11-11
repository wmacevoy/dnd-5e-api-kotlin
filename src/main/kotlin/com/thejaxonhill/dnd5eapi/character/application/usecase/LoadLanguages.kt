package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.LanguageExample
import com.thejaxonhill.dnd5eapi.character.domain.LanguageRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadLanguages(private val languageRepository: LanguageRepository) {
    fun load(page: Page, example: LanguageExample?) =
        languageRepository.loadAll(page, example).map { it.toView() }
}