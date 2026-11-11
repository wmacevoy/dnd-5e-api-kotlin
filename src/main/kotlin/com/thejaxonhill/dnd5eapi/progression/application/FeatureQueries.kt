package com.thejaxonhill.dnd5eapi.progression.application

import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.FeatureExample
import com.thejaxonhill.dnd5eapi.progression.domain.FeatureRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import org.springframework.stereotype.Service

@Service
class FeatureQueries(private val repository: FeatureRepository) {
    fun list(page: Page, example: FeatureExample?) = repository.loadAll(page, example).map { it.toView() }

    fun byIndex(index: String) =
        Index.parse(index)?.let { repository.loadByIndex(it) }?.toView() ?: throw NoSuchElementException()
}
