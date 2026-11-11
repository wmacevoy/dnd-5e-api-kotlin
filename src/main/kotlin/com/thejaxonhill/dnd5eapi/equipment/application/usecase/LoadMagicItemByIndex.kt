package com.thejaxonhill.dnd5eapi.equipment.application.usecase

import com.thejaxonhill.dnd5eapi.equipment.application.dto.toView
import com.thejaxonhill.dnd5eapi.equipment.domain.MagicItemRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadMagicItemByIndex(private val magicItemRepository: MagicItemRepository) {
    fun load(index: String) = magicItemRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}