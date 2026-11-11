package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.character.domain.Language
import com.thejaxonhill.dnd5eapi.character.domain.LanguageExample
import com.thejaxonhill.dnd5eapi.character.domain.LanguageRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoLanguageRepository(private val repository: LanguageMongoRepository) : LanguageRepository {
    override fun loadByIndex(index: Index): Language? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: LanguageExample?): PagedModel<Language> =
        repository.findAll(
            Example.of(example?.toLanguageDocument() ?: LanguageDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun LanguageDocument.toDomain() = Language(
    desc = desc,
    index = Index.of(index!!),
    name = name!!,
    script = script,
    type = type!!,
    typicalSpeakers = typicalSpeakers!!,
    url = url!!,
)

fun LanguageExample.toLanguageDocument(): LanguageDocument = LanguageDocument(
    name = name
)
