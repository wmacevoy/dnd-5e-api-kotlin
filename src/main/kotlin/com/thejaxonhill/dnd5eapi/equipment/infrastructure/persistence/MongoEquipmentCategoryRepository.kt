package com.thejaxonhill.dnd5eapi.equipment.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentCategory
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentCategoryExample
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentCategoryRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoEquipmentCategoryRepository(private val repository: EquipmentCategoryMongoRepository) : EquipmentCategoryRepository {
    override fun loadByIndex(index: Index): EquipmentCategory? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: EquipmentCategoryExample?): PagedModel<EquipmentCategory> =
        repository.findAll(
            Example.of(example?.toEquipmentCategoryDocument() ?: EquipmentCategoryDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun EquipmentCategoryDocument.toDomain() = EquipmentCategory(
    equipment = equipment!!,
    index = Index.of(index!!),
    name = name!!,
    url = url!!,
)

fun EquipmentCategoryExample.toEquipmentCategoryDocument(): EquipmentCategoryDocument = EquipmentCategoryDocument(
    name = name
)
