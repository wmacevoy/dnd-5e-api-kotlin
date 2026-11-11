package com.thejaxonhill.dnd5eapi.character.application

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.RaceExample
import com.thejaxonhill.dnd5eapi.character.domain.RaceRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class RaceQueries(private val repository: RaceRepository) {
    fun list(page: Page, example: RaceExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
