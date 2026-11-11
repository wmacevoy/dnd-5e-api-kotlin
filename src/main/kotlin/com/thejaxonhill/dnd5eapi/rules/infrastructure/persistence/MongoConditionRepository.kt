package com.thejaxonhill.dnd5eapi.rules.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.rules.domain.Condition
import com.thejaxonhill.dnd5eapi.rules.domain.ConditionExample
import com.thejaxonhill.dnd5eapi.rules.domain.ConditionRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoConditionRepository(private val repository: ConditionMongoRepository) : ConditionRepository {
    override fun loadByIndex(index: String): Condition? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: ConditionExample?): PagedModel<Condition> =
        repository.findAll(
            Example.of(example?.toConditionDocument() ?: ConditionDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun ConditionDocument.toDomain() = Condition(
    index = index!!,
    name = name!!,
    desc = desc!!,
    url = url!!,
)

fun ConditionExample.toConditionDocument(): ConditionDocument = ConditionDocument(
    name = name
)
