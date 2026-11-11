package com.thejaxonhill.dnd5eapi.progression.application.usecase

import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.FeatureRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadFeatureByIndex(private val featureRepository: FeatureRepository) {
    fun load(index: String) = featureRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}