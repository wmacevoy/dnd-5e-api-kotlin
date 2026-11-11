package com.thejaxonhill.dnd5eapi.character.application

import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.LanguageExample
import com.thejaxonhill.dnd5eapi.character.domain.LanguageRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class LanguageQueries(private val repository: LanguageRepository) {
    fun list(page: Page, example: LanguageExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
