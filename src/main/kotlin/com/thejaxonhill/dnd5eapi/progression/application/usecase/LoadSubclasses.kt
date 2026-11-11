package com.thejaxonhill.dnd5eapi.progression.application.usecase

import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.progression.application.dto.toView
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassExample
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassRepository

@UseCase
class LoadSubclasses(private val subclassRepository: SubclassRepository) {
    fun load(page: Page, example: SubclassExample?) =
        subclassRepository.loadAll(page, example).map { it.toView() }
}