package com.thejaxonhill.dnd5eapi.rules.application

import com.thejaxonhill.dnd5eapi.rules.application.dto.toView
import com.thejaxonhill.dnd5eapi.rules.domain.DamageTypeExample
import com.thejaxonhill.dnd5eapi.rules.domain.DamageTypeRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class DamageTypeQueries(private val repository: DamageTypeRepository) {
    fun list(page: Page, example: DamageTypeExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
