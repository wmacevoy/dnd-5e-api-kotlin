package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import com.thejaxonhill.dnd5eapi.character.domain.Subrace
import com.thejaxonhill.dnd5eapi.character.domain.SubraceExample
import com.thejaxonhill.dnd5eapi.character.domain.SubraceRepository
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoSubraceRepository(private val repository: SubraceMongoRepository) : SubraceRepository {
    override fun loadByIndex(index: Index): Subrace? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: SubraceExample?): PagedModel<Subrace> =
        repository.findAll(
            Example.of(example?.toSubraceDocument() ?: SubraceDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun SubraceDocument.toDomain() = Subrace(
    abilityBonuses = abilityBonuses!!,
    desc = desc!!,
    index = Index.of(index!!),
    languages = languages,
    languageOptions = languageOptions,
    name = name!!,
    race = race!!,
    racialTraits = racialTraits!!,
    startingProficiencies = startingProficiencies,
    url = url!!,
)

fun SubraceExample.toSubraceDocument(): SubraceDocument = SubraceDocument(
    name = name
)
