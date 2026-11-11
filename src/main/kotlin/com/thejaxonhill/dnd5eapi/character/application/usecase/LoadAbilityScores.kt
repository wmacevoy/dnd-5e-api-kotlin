package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.AbilityScoreExample
import com.thejaxonhill.dnd5eapi.character.domain.AbilityScoreRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadAbilityScores(private val abilityScoreRepository: AbilityScoreRepository) {
    fun load(page: Page, example: AbilityScoreExample?) =
        abilityScoreRepository.loadAll(page, example).map { it.toView() }
}