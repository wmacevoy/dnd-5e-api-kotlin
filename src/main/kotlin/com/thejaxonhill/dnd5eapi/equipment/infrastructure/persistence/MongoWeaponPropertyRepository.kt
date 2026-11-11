package com.thejaxonhill.dnd5eapi.equipment.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponProperty
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponPropertyExample
import com.thejaxonhill.dnd5eapi.equipment.domain.WeaponPropertyRepository
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoWeaponPropertyRepository(private val repository: WeaponPropertyMongoRepository) : WeaponPropertyRepository {
    override fun loadByIndex(index: Index): WeaponProperty? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: WeaponPropertyExample?): PagedModel<WeaponProperty> =
        repository.findAll(
            Example.of(example?.toWeaponPropertyDocument() ?: WeaponPropertyDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun WeaponPropertyDocument.toDomain() = WeaponProperty(
    desc = desc!!,
    index = Index.of(index!!),
    name = name!!,
    url = url!!,
)

fun WeaponPropertyExample.toWeaponPropertyDocument(): WeaponPropertyDocument = WeaponPropertyDocument(
    name = name
)
