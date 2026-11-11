package com.thejaxonhill.dnd5eapi.rules.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.rules.domain.DamageType
import com.thejaxonhill.dnd5eapi.rules.domain.DamageTypeExample
import com.thejaxonhill.dnd5eapi.rules.domain.DamageTypeRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoDamageTypeRepository(private val repository: DamageTypeMongoRepository) : DamageTypeRepository {
    override fun loadByIndex(index: String): DamageType? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: DamageTypeExample?): PagedModel<DamageType> =
        repository.findAll(
            Example.of(example?.toDamageTypeDocument() ?: DamageTypeDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun DamageTypeDocument.toDomain() = DamageType(
    index = index!!,
    name = name!!,
    desc = desc!!,
    url = url!!,
)

fun DamageTypeExample.toDamageTypeDocument(): DamageTypeDocument = DamageTypeDocument(
    name = name
)
