package com.thejaxonhill.dnd5eapi.rules.application.usecase

import com.thejaxonhill.dnd5eapi.rules.application.dto.toView
import com.thejaxonhill.dnd5eapi.rules.domain.ConditionExample
import com.thejaxonhill.dnd5eapi.rules.domain.ConditionRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page

@UseCase
class LoadConditions(private val conditionRepository: ConditionRepository) {
    fun load(page: Page, example: ConditionExample?) =
        conditionRepository.loadAll(page, example).map { it.toView() }
}