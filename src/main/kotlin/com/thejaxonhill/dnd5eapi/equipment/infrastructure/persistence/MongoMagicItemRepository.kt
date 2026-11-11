package com.thejaxonhill.dnd5eapi.equipment.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.equipment.domain.MagicItem
import com.thejaxonhill.dnd5eapi.equipment.domain.MagicItemExample
import com.thejaxonhill.dnd5eapi.equipment.domain.MagicItemRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoMagicItemRepository(private val repository: MagicItemMongoRepository) : MagicItemRepository {
    override fun loadByIndex(index: String): MagicItem? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: MagicItemExample?): PagedModel<MagicItem> =
        repository.findAll(
            Example.of(example?.toMagicItemDocument() ?: MagicItemDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun MagicItemDocument.toDomain() = MagicItem(
    desc = desc!!,
    equipmentCategory = equipmentCategory!!,
    image = image,
    index = index!!,
    name = name!!,
    rarity = rarity!!,
    url = url!!,
    variants = variants!!,
    variant = variant!!,
)

fun MagicItemExample.toMagicItemDocument(): MagicItemDocument = MagicItemDocument(
    name = name
)
