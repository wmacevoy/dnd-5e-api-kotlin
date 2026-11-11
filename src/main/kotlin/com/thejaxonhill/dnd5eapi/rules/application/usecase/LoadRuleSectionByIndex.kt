package com.thejaxonhill.dnd5eapi.rules.application.usecase

import com.thejaxonhill.dnd5eapi.rules.application.dto.toView
import com.thejaxonhill.dnd5eapi.rules.domain.RuleSectionRepository
import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase

@UseCase
class LoadRuleSectionByIndex(private val ruleSectionRepository: RuleSectionRepository) {
    fun load(index: String) = ruleSectionRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}