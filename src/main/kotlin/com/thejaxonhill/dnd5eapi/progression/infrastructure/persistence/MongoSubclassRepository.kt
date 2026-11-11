package com.thejaxonhill.dnd5eapi.progression.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import com.thejaxonhill.dnd5eapi.progression.domain.Subclass
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassExample
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassRepository
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoSubclassRepository(private val repository: SubclassMongoRepository) : SubclassRepository {
    override fun loadByIndex(index: Index): Subclass? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: SubclassExample?): PagedModel<Subclass> =
        repository.findAll(
            Example.of(example?.toSubclassDocument() ?: SubclassDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun SubclassDocument.toDomain() = Subclass(
    characterClass = characterClass!!,
    desc = desc!!,
    index = Index.of(index!!),
    name = name!!,
    spells = spells,
    subclassFlavor = subclassFlavor!!,
    subclassLevels = subclassLevels!!,
    url = url!!,
)

fun SubclassExample.toSubclassDocument(): SubclassDocument = SubclassDocument(
    name = name
)
