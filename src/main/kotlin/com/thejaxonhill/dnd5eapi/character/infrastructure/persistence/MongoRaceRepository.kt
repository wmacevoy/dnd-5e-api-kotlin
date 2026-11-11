package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.character.domain.Race
import com.thejaxonhill.dnd5eapi.character.domain.RaceExample
import com.thejaxonhill.dnd5eapi.character.domain.RaceRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoRaceRepository(private val repository: RaceMongoRepository) : RaceRepository {
    override fun loadByIndex(index: Index): Race? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: RaceExample?): PagedModel<Race> =
        repository.findAll(
            Example.of(example?.toRaceDocument() ?: RaceDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun RaceDocument.toDomain() = Race(
    abilityBonusOptions = abilityBonusOptions,
    abilityBonuses = abilityBonuses!!,
    age = age!!,
    alignment = alignment!!,
    index = Index.of(index!!),
    languageDesc = languageDesc!!,
    languageOptions = languageOptions,
    languages = languages!!,
    name = name!!,
    size = size!!,
    sizeDescription = sizeDescription!!,
    speed = speed!!,
    startingProficiencies = startingProficiencies,
    startingProficiencyOptions = startingProficiencyOptions,
    subraces = subraces,
    traits = traits,
    url = url!!,
)

fun RaceExample.toRaceDocument(): RaceDocument = RaceDocument(
    name = name
)
