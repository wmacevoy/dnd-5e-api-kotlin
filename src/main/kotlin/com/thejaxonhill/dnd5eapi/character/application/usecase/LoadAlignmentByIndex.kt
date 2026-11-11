package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.AlignmentRepository

import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadAlignmentByIndex(private val alignmentRepository: AlignmentRepository) {
    fun load(index: String) = alignmentRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}