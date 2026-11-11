package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.character.domain.Feat
import com.thejaxonhill.dnd5eapi.character.domain.FeatExample
import com.thejaxonhill.dnd5eapi.character.domain.FeatRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoFeatRepository(private val repository: FeatMongoRepository) : FeatRepository {
    override fun loadByIndex(index: Index): Feat? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: FeatExample?): PagedModel<Feat> =
        repository.findAll(
            Example.of(example?.toFeatDocument() ?: FeatDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun FeatDocument.toDomain() = Feat(
    index = Index.of(index!!),
    name = name!!,
    prerequisites = prerequisites!!,
    desc = desc!!,
    url = url!!,
)

fun FeatExample.toFeatDocument(): FeatDocument = FeatDocument(
    name = name
)
