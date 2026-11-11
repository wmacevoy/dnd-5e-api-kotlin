package com.thejaxonhill.dnd5eapi.spellcasting.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.spellcasting.domain.MagicSchool
import com.thejaxonhill.dnd5eapi.spellcasting.domain.MagicSchoolExample
import com.thejaxonhill.dnd5eapi.spellcasting.domain.MagicSchoolRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoMagicSchoolRepository(private val repository: MagicSchoolMongoRepository) : MagicSchoolRepository {
    override fun loadByIndex(index: Index): MagicSchool? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: MagicSchoolExample?): PagedModel<MagicSchool> =
        repository.findAll(
            Example.of(example?.toMagicSchoolDocument() ?: MagicSchoolDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun MagicSchoolDocument.toDomain() = MagicSchool(
    desc = desc!!,
    index = Index.of(index!!),
    name = name!!,
    url = url!!,
)

fun MagicSchoolExample.toMagicSchoolDocument(): MagicSchoolDocument = MagicSchoolDocument(
    name = name
)
