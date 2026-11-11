package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import com.thejaxonhill.dnd5eapi.character.domain.Trait
import com.thejaxonhill.dnd5eapi.character.domain.TraitExample
import com.thejaxonhill.dnd5eapi.character.domain.TraitRepository
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoTraitRepository(private val repository: TraitMongoRepository) : TraitRepository {
    override fun loadByIndex(index: String): Trait? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: TraitExample?): PagedModel<Trait> =
        repository.findAll(
            Example.of(example?.toTraitDocument() ?: TraitDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun TraitDocument.toDomain() = Trait(
    desc = desc!!,
    index = index!!,
    name = name!!,
    proficiencies = proficiencies,
    proficiencyChoices = proficiencyChoices,
    languageOptions = languageOptions,
    races = races!!,
    subraces = subraces!!,
    parent = parent,
    traitSpecific = traitSpecific,
    url = url!!,
)

fun TraitExample.toTraitDocument(): TraitDocument = TraitDocument(
    name = name
)
