package com.thejaxonhill.dnd5eapi.equipment.application

import com.thejaxonhill.dnd5eapi.equipment.application.dto.toView
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentCategoryExample
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentCategoryRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class EquipmentCategoryQueries(private val repository: EquipmentCategoryRepository) {
    fun list(page: Page, example: EquipmentCategoryExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
