package com.thejaxonhill.dnd5eapi

import org.bson.Document
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * The only code that reads MongoDB (REQ3/DAT-1). It returns upstream's documents as stored. Names
 * come in camelCase, as clients send them, and are queried in upstream's snake_case.
 */
@Component
class Store(private val mongo: MongoTemplate) {
    /** One page of a resource's items whose fields equal [filters], in stored order unless [pageable] sorts. */
    fun find(resource: Resource, filters: Map<String, Any>, pageable: Pageable): List<Document> {
        val query = Query()
        for ((name, value) in filters) query.addCriteria(Criteria.where(snakeCase(name)).`is`(value))
        return mongo.find(query.with(upstream(pageable)), Document::class.java, resource.collection)
    }

    fun findByIndex(resource: Resource, index: String): Document? =
        mongo.findOne(Query(Criteria.where("index").`is`(index)), Document::class.java, resource.collection)

    /** Items whose name contains [text], ignoring case (REQ1/API-5). */
    fun findByName(resource: Resource, text: String, pageable: Pageable): List<Document> {
        val query = Query(Criteria.where("name").regex(".*" + Pattern.quote(text) + ".*", "i"))
        return mongo.find(query.with(upstream(pageable)), Document::class.java, resource.collection)
    }

    private fun upstream(pageable: Pageable): Pageable = PageRequest.of(
        pageable.pageNumber,
        pageable.pageSize,
        Sort.by(pageable.sort.map { Sort.Order(it.direction, snakeCase(it.property)) }.toList()),
    )
}
