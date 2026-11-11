package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import com.thejaxonhill.dnd5eapi.character.domain.Skill
import com.thejaxonhill.dnd5eapi.character.domain.SkillExample
import com.thejaxonhill.dnd5eapi.character.domain.SkillRepository
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoSkillRepository(private val repository: SkillMongoRepository) : SkillRepository {
    override fun loadByIndex(index: Index): Skill? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: SkillExample?): PagedModel<Skill> =
        repository.findAll(
            Example.of(example?.toSkillDocument() ?: SkillDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun SkillDocument.toDomain() = Skill(
    abilityScore = abilityScore!!,
    desc = desc!!,
    index = Index.of(index!!),
    name = name!!,
    url = url!!,
)

fun SkillExample.toSkillDocument(): SkillDocument = SkillDocument(
    name = name
)
