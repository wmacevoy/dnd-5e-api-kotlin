package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.character.domain.Alignment
import com.thejaxonhill.dnd5eapi.character.domain.AlignmentExample
import com.thejaxonhill.dnd5eapi.character.domain.AlignmentRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoAlignmentRepository(private val repository: AlignmentMongoRepository) : AlignmentRepository {
    override fun loadByIndex(index: Index): Alignment? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: AlignmentExample?): PagedModel<Alignment> =
        repository.findAll(
            Example.of(example?.toAlignmentDocument() ?: AlignmentDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun AlignmentDocument.toDomain() = Alignment(
    index = Index.of(index!!),
    name = name,
    abbreviation = abbreviation,
    desc = desc,
    url = url,
)

fun AlignmentExample.toAlignmentDocument(): AlignmentDocument = AlignmentDocument(
    name = name,
    abbreviation = abbreviation
)