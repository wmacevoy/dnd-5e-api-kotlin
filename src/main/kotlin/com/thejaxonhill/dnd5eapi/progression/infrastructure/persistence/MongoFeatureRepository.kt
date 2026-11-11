package com.thejaxonhill.dnd5eapi.progression.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.progression.domain.Feature
import com.thejaxonhill.dnd5eapi.progression.domain.FeatureExample
import com.thejaxonhill.dnd5eapi.progression.domain.FeatureRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoFeatureRepository(private val repository: FeatureMongoRepository) : FeatureRepository {
    override fun loadByIndex(index: String): Feature? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: FeatureExample?): PagedModel<Feature> =
        repository.findAll(
            Example.of(example?.toFeatureDocument() ?: FeatureDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun FeatureDocument.toDomain() = Feature(
    characterClass = characterClass!!,
    desc = desc!!,
    parent = parent,
    index = index!!,
    level = level!!,
    name = name!!,
    prerequisite = prerequisite,
    reference = reference,
    subclass = subclass,
    featureSpecific = featureSpecific,
    url = url!!,
)

fun FeatureExample.toFeatureDocument(): FeatureDocument = FeatureDocument(
    name = name
)
