package com.thejaxonhill.dnd5eapi.equipment.application

import com.thejaxonhill.dnd5eapi.equipment.application.dto.toView
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponMasteryPropertyExample
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponMasteryPropertyRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class WeaponMasteryPropertyQueries(private val repository: WeaponMasteryPropertyRepository) {
    fun list(page: Page, example: WeaponMasteryPropertyExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
