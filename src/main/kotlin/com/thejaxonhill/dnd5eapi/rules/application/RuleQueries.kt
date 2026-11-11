package com.thejaxonhill.dnd5eapi.rules.application

import com.thejaxonhill.dnd5eapi.rules.application.dto.toView
import com.thejaxonhill.dnd5eapi.rules.domain.RuleExample
import com.thejaxonhill.dnd5eapi.rules.domain.RuleRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class RuleQueries(private val repository: RuleRepository) {
    fun list(page: Page, example: RuleExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
