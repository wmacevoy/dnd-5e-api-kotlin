package com.thejaxonhill.dnd5eapi.bestiary.application.usecase

import com.thejaxonhill.dnd5eapi.bestiary.application.dto.toView
import com.thejaxonhill.dnd5eapi.bestiary.domain.MonsterExample
import com.thejaxonhill.dnd5eapi.bestiary.domain.MonsterRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadMonsters(private val monsterRepository: MonsterRepository) {
    fun load(page: Page, example: MonsterExample?) =
        monsterRepository.loadAll(page, example).map { it.toView() }
}