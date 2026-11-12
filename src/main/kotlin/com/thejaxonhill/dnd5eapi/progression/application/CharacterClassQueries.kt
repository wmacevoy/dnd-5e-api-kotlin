package com.thejaxonhill.dnd5eapi.progression.application

import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.CharacterClassExample
import com.thejaxonhill.dnd5eapi.progression.domain.CharacterClassRepository
import com.thejaxonhill.dnd5eapi.progression.domain.checkMultiClassing
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class CharacterClassQueries(private val repository: CharacterClassRepository) {
    fun list(page: Page, example: CharacterClassExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()

    fun multiClassingCheck(index: String, scores: Map<String, Int?>) =
        (Index.parse(index)?.let { repository.loadByIndex(it) } ?: throw NoSuchElementException())
            .checkMultiClassing(scores).toView()
}
