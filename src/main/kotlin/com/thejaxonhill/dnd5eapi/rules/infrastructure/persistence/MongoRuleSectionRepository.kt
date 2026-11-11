package com.thejaxonhill.dnd5eapi.rules.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.rules.domain.RuleSection
import com.thejaxonhill.dnd5eapi.rules.domain.RuleSectionExample
import com.thejaxonhill.dnd5eapi.rules.domain.RuleSectionRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoRuleSectionRepository(private val repository: RuleSectionMongoRepository) : RuleSectionRepository {
    override fun loadByIndex(index: Index): RuleSection? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: RuleSectionExample?): PagedModel<RuleSection> =
        repository.findAll(
            Example.of(example?.toRuleSectionDocument() ?: RuleSectionDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun RuleSectionDocument.toDomain() = RuleSection(
    desc = desc!!,
    index = Index.of(index!!),
    name = name!!,
    url = url!!,
)

fun RuleSectionExample.toRuleSectionDocument(): RuleSectionDocument = RuleSectionDocument(
    name = name
)
