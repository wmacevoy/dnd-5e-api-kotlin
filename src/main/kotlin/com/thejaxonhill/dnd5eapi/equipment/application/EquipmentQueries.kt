package com.thejaxonhill.dnd5eapi.equipment.application

import com.thejaxonhill.dnd5eapi.equipment.application.dto.toView
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentExample
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class EquipmentQueries(private val repository: EquipmentRepository) {
    fun list(page: Page, example: EquipmentExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()

    fun search(page: Page, query: String?) = repository.loadAll(page, query).map { it.toView() }
}
