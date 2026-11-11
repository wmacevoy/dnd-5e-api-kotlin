package com.thejaxonhill.dnd5eapi.progression.application.usecase

import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassRepository

@UseCase
class LoadSubclassByIndex(private val subclassRepository: SubclassRepository) {
    fun load(index: String) = subclassRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}