package com.thejaxonhill.dnd5eapi.equipment.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponMasteryProperty
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponMasteryPropertyExample
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponMasteryPropertyRepository
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoWeaponMasteryPropertyRepository(private val repository: WeaponMasteryPropertyMongoRepository) : WeaponMasteryPropertyRepository {
    override fun loadByIndex(index: Index): WeaponMasteryProperty? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: WeaponMasteryPropertyExample?): PagedModel<WeaponMasteryProperty> =
        repository.findAll(
            Example.of(example?.toWeaponMasteryPropertyDocument() ?: WeaponMasteryPropertyDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun WeaponMasteryPropertyDocument.toDomain() = WeaponMasteryProperty(
    description = description!!,
    index = Index.of(index!!),
    name = name!!,
    url = url!!,
)

fun WeaponMasteryPropertyExample.toWeaponMasteryPropertyDocument(): WeaponMasteryPropertyDocument = WeaponMasteryPropertyDocument(
    name = name
)
