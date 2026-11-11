package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.FeatExample
import com.thejaxonhill.dnd5eapi.character.domain.FeatRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadFeats(private val featRepository: FeatRepository) {
    fun load(page: Page, example: FeatExample?) =
        featRepository.loadAll(page, example).map { it.toView() }
}