package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.character.domain.AbilityScore
import com.thejaxonhill.dnd5eapi.character.domain.AbilityScoreExample
import com.thejaxonhill.dnd5eapi.character.domain.AbilityScoreRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoAbilityScoreRepository(private val repository: AbilityScoreMongoRepository) : AbilityScoreRepository {
    override fun loadByIndex(index: Index): AbilityScore? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: AbilityScoreExample?): PagedModel<AbilityScore> =
        repository.findAll(
            Example.of(example?.toAbilityScoreDocument() ?: AbilityScoreDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun AbilityScoreDocument.toDomain() = AbilityScore(
    desc = desc,
    fullName = fullName,
    index = Index.of(index!!),
    name = name,
    skills = skills,
    url = url,
)

fun AbilityScoreExample.toAbilityScoreDocument(): AbilityScoreDocument = AbilityScoreDocument(name = name)

