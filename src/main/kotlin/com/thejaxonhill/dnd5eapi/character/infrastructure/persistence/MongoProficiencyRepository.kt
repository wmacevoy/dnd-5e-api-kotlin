package com.thejaxonhill.dnd5eapi.character.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.character.domain.Proficiency
import com.thejaxonhill.dnd5eapi.character.domain.ProficiencyExample
import com.thejaxonhill.dnd5eapi.character.domain.ProficiencyRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoProficiencyRepository(private val repository: ProficiencyMongoRepository) : ProficiencyRepository {
    override fun loadByIndex(index: String): Proficiency? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: ProficiencyExample?): PagedModel<Proficiency> =
        repository.findAll(
            Example.of(example?.toProficiencyDocument() ?: ProficiencyDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun ProficiencyDocument.toDomain() = Proficiency(
    classes = classes,
    index = index!!,
    name = name!!,
    races = races,
    reference = reference!!,
    type = type!!,
    url = url!!,
)

fun ProficiencyExample.toProficiencyDocument(): ProficiencyDocument = ProficiencyDocument(
    name = name
)
