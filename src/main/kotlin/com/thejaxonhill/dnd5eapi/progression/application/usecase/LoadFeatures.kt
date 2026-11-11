package com.thejaxonhill.dnd5eapi.progression.application.usecase

import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.FeatureExample
import com.thejaxonhill.dnd5eapi.progression.domain.FeatureRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadFeatures(private val featureRepository: FeatureRepository) {
    fun load(page: Page, example: FeatureExample?) =
        featureRepository.loadAll(page, example).map { it.toView() }
}