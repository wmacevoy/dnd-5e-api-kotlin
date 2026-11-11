package com.thejaxonhill.dnd5eapi.rules.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.rules.domain.Rule
import com.thejaxonhill.dnd5eapi.rules.domain.RuleExample
import com.thejaxonhill.dnd5eapi.rules.domain.RuleRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoRuleRepository(private val repository: RuleMongoRepository) : RuleRepository {
    override fun loadByIndex(index: String): Rule? = repository.findByIndex(index)?.toDomain()

    override fun loadAll(page: Page, example: RuleExample?): PagedModel<Rule> =
        repository.findAll(
            Example.of(example?.toRuleDocument() ?: RuleDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }
}

fun RuleDocument.toDomain() = Rule(
    desc = desc!!,
    index = index!!,
    name = name!!,
    subsections = subsections!!,
    url = url!!,
)

fun RuleExample.toRuleDocument(): RuleDocument = RuleDocument(
    name = name
)
