package com.thejaxonhill.dnd5eapi.bestiary.application

import com.thejaxonhill.dnd5eapi.bestiary.application.dto.toView
import com.thejaxonhill.dnd5eapi.bestiary.domain.MonsterExample
import com.thejaxonhill.dnd5eapi.bestiary.domain.MonsterRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class MonsterQueries(private val repository: MonsterRepository) {
    fun list(page: Page, example: MonsterExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
