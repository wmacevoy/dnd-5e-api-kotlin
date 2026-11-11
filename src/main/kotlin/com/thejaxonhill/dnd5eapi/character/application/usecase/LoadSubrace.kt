package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.SubraceRepository

@UseCase
class LoadSubraceByIndex(private val subraceRepository: SubraceRepository) {
    fun load(index: String) = subraceRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}