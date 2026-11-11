package com.thejaxonhill.dnd5eapi.character.application

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.FeatExample
import com.thejaxonhill.dnd5eapi.character.domain.FeatRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class FeatQueries(private val repository: FeatRepository) {
    fun list(page: Page, example: FeatExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
