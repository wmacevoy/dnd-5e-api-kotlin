package com.thejaxonhill.dnd5eapi.character.application.usecase

import com.thejaxonhill.dnd5eapi.shared.application.stereotype.UseCase
import com.thejaxonhill.dnd5eapi.character.application.dto.toView
import com.thejaxonhill.dnd5eapi.character.domain.SkillRepository

@UseCase
class LoadSkillByIndex(private val skillRepository: SkillRepository) {
    fun load(index: String) = skillRepository.loadByIndex(index)?.toView() ?: throw NoSuchElementException()
}