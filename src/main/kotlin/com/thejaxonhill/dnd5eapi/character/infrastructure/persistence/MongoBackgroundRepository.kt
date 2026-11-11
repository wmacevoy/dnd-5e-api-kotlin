package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.character.domain.Background
import com.thejaxonhill.dnd5eapi.character.domain.BackgroundExample
import com.thejaxonhill.dnd5eapi.character.domain.BackgroundRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoBackgroundRepository(private val repository: BackgroundMongoRepository) : BackgroundRepository {
    override fun loadByIndex(index: String): Background? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: BackgroundExample?): PagedModel<Background> =
        repository.findAll(
            Example.of(example?.toBackgroundDocument() ?: BackgroundDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun BackgroundDocument.toDomain() = Background(
    index = index!!,
    name = name!!,
    startingProficiencies = startingProficiencies!!,
    languageOptions = languageOptions!!,
    startingEquipment = startingEquipment!!,
    startingEquipmentOptions = startingEquipmentOptions!!,
    feature = feature!!,
    personalityTraits = personalityTraits!!,
    ideals = ideals!!,
    bonds = bonds!!,
    flaws = flaws!!,
    url = url!!,
)

fun BackgroundExample.toBackgroundDocument(): BackgroundDocument = BackgroundDocument(
    name = name
)
