package com.thejaxonhill.dnd5eapi.equipment.application.usecase

import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.equipment.application.dto.toView
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponPropertyRepository

@UseCase
class LoadWeaponPropertyByIndex(private val weaponPropertyRepository: WeaponPropertyRepository) {
    fun load(index: String) = weaponPropertyRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}