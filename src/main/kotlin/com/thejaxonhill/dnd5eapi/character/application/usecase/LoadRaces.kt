package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.RaceExample
import com.thejaxonhill.dnd5eapi.character.domain.RaceRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadRaces(private val raceRepository: RaceRepository) {
    fun load(page: Page, example: RaceExample?) =
        raceRepository.loadAll(page, example).map { it.toView() }
}