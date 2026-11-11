package com.thejaxonhill.dnd5eapi.rules.application

import com.thejaxonhill.dnd5eapi.rules.application.dto.toView
import com.thejaxonhill.dnd5eapi.rules.domain.RuleSectionExample
import com.thejaxonhill.dnd5eapi.rules.domain.RuleSectionRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class RuleSectionQueries(private val repository: RuleSectionRepository) {
    fun list(page: Page, example: RuleSectionExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
