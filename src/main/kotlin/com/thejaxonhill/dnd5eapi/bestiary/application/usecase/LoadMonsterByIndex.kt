package com.thejaxonhill.dnd5eapi.bestiary.application.usecase

import com.thejaxonhill.dnd5eapi.bestiary.application.dto.toView
import com.thejaxonhill.dnd5eapi.bestiary.domain.MonsterRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadMonsterByIndex(private val monsterRepository: MonsterRepository) {
    fun load(index: String) = monsterRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}