package com.thejaxonhill.dnd5eapi.progression.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.progression.domain.Level
import com.thejaxonhill.dnd5eapi.progression.domain.LevelExample
import com.thejaxonhill.dnd5eapi.progression.domain.LevelRepository
import com.thejaxonhill.dnd5eapi.progression.domain.SpellcastingSlots
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoLevelRepository(private val repository: LevelMongoRepository) : LevelRepository {
    override fun loadByIndex(index: Index): Level? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: LevelExample?): PagedModel<Level> =
        repository.findAll(
            Example.of(example?.toLevelDocument() ?: LevelDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun LevelDocument.toDomain() = Level(
    abilityScoreBonuses = abilityScoreBonuses,
    characterClass = characterClass!!,
    classSpecific = classSpecific,
    features = features!!,
    index = Index.of(index!!),
    level = level!!,
    profBonus = profBonus,
    spellcasting = spellcasting?.toDomain(),
    subclass = subclass,
    subclassSpecific = subclassSpecific,
    url = url!!,
)

fun SpellcastingSlotsDocument.toDomain() = SpellcastingSlots(
    cantripsKnown = cantripsKnown,
    spellSlotsLevel1 = spellSlotsLevel1,
    spellSlotsLevel2 = spellSlotsLevel2,
    spellSlotsLevel3 = spellSlotsLevel3,
    spellSlotsLevel4 = spellSlotsLevel4,
    spellSlotsLevel5 = spellSlotsLevel5,
    spellSlotsLevel6 = spellSlotsLevel6,
    spellSlotsLevel7 = spellSlotsLevel7,
    spellSlotsLevel8 = spellSlotsLevel8,
    spellSlotsLevel9 = spellSlotsLevel9,
    spellsKnown = spellsKnown,
)

fun LevelExample.toLevelDocument(): LevelDocument = LevelDocument(
    level = level
)
