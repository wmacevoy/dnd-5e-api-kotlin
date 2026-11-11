package com.thejaxonhill.dnd5eapi.equipment.application

import com.thejaxonhill.dnd5eapi.equipment.application.dto.toView
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponPropertyExample
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponPropertyRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class WeaponPropertyQueries(private val repository: WeaponPropertyRepository) {
    fun list(page: Page, example: WeaponPropertyExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
