package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.AbilityScoreRepository

import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadAbilityScoreByIndex(private val abilityScoreRepository: AbilityScoreRepository) {
    fun load(index: String) = abilityScoreRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}