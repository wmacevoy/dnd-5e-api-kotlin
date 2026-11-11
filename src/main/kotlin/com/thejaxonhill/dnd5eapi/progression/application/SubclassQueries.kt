package com.thejaxonhill.dnd5eapi.progression.application

import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassExample
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class SubclassQueries(private val repository: SubclassRepository) {
    fun list(page: Page, example: SubclassExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
