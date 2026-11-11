package com.thejaxonhill.dnd5eapi.progression.application

import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.LevelExample
import com.thejaxonhill.dnd5eapi.progression.domain.LevelRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class LevelQueries(private val repository: LevelRepository) {
    fun list(page: Page, example: LevelExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
