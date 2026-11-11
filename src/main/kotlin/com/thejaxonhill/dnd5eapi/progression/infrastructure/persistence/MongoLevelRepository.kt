package com.thejaxonhill.dnd5eapi.progression.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.progression.domain.Level
import com.thejaxonhill.dnd5eapi.progression.domain.LevelExample
import com.thejaxonhill.dnd5eapi.progression.domain.LevelRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoLevelRepository(private val repository: LevelMongoRepository) : LevelRepository {
    override fun loadByIndex(index: String): Level? = repository.findByIndex(index)?.toDomain()

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
    index = index!!,
    level = level!!,
    profBonus = profBonus,
    spellcasting = spellcasting,
    subclass = subclass,
    subclassSpecific = subclassSpecific,
    url = url!!,
)

fun LevelExample.toLevelDocument(): LevelDocument = LevelDocument(
    level = level
)
