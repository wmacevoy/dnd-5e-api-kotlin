package com.thejaxonhill.dnd5eapi.bestiary.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.bestiary.domain.Monster
import com.thejaxonhill.dnd5eapi.bestiary.domain.MonsterExample
import com.thejaxonhill.dnd5eapi.bestiary.domain.MonsterRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoMonsterRepository(private val repository: MonsterMongoRepository) : MonsterRepository {
    override fun loadByIndex(index: String): Monster? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: MonsterExample?): PagedModel<Monster> =
        repository.findAll(
            Example.of(example?.toMonsterDocument() ?: MonsterDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun MonsterDocument.toDomain() = Monster(
    actions = actions,
    alignment = alignment!!,
    armorClass = armorClass!!,
    challengeRating = challengeRating!!,
    charisma = charisma!!,
    conditionImmunities = conditionImmunities!!,
    constitution = constitution!!,
    damageImmunities = damageImmunities!!,
    damageResistances = damageResistances!!,
    damageVulnerabilities = damageVulnerabilities!!,
    dexterity = dexterity!!,
    forms = forms,
    hitDice = hitDice!!,
    hitPoints = hitPoints!!,
    hitPointsRoll = hitPointsRoll!!,
    image = image,
    index = index!!,
    intelligence = intelligence!!,
    languages = languages!!,
    legendaryActions = legendaryActions,
    name = name!!,
    proficiencies = proficiencies!!,
    reactions = reactions,
    senses = senses!!,
    size = size!!,
    specialAbilities = specialAbilities,
    speed = speed!!,
    strength = strength!!,
    subtype = subtype,
    type = type!!,
    url = url!!,
    wisdom = wisdom!!,
    xp = xp!!,
)

fun MonsterExample.toMonsterDocument(): MonsterDocument = MonsterDocument(
    name = name
)
