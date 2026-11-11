package com.thejaxonhill.dnd5eapi.spellcasting.application

import com.thejaxonhill.dnd5eapi.spellcasting.application.dto.toView
import com.thejaxonhill.dnd5eapi.spellcasting.domain.SpellExample
import com.thejaxonhill.dnd5eapi.spellcasting.domain.SpellRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class SpellQueries(private val repository: SpellRepository) {
    fun list(page: Page, example: SpellExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
