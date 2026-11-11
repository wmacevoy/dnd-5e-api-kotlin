package com.thejaxonhill.dnd5eapi.progression.application.usecase

import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.LevelRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadLevelByIndex(private val levelRepository: LevelRepository) {
    fun load(index: String) = levelRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}