package com.thejaxonhill.dnd5eapi.character.application

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.SubraceExample
import com.thejaxonhill.dnd5eapi.character.domain.SubraceRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class SubraceQueries(private val repository: SubraceRepository) {
    fun list(page: Page, example: SubraceExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
