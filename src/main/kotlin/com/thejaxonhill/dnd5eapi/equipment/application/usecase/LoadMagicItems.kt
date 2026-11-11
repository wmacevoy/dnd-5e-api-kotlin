package com.thejaxonhill.dnd5eapi.equipment.application.usecase

import com.thejaxonhill.dnd5eapi.equipment.application.dto.toView
import com.thejaxonhill.dnd5eapi.equipment.domain.MagicItemExample
import com.thejaxonhill.dnd5eapi.equipment.domain.MagicItemRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadMagicItems(private val magicItemRepository: MagicItemRepository) {
    fun load(page: Page, example: MagicItemExample?) =
        magicItemRepository.loadAll(page, example).map { it.toView() }
}