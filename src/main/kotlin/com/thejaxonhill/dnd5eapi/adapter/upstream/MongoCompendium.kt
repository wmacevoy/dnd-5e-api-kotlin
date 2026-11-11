package com.thejaxonhill.dnd5eapi.adapter.upstream

import com.thejaxonhill.dnd5eapi.compendium.Compendium
import com.thejaxonhill.dnd5eapi.compendium.Entry
import com.thejaxonhill.dnd5eapi.compendium.Index
import com.thejaxonhill.dnd5eapi.compendium.Kind
import com.thejaxonhill.dnd5eapi.compendium.PageRequest
import org.bson.Document
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * The Compendium over 5e-database's MongoDB (REQ3/DAT-1). Upstream's spelling lives here and nowhere
 * else (REQ2/SHAPE-4): its collection names, its snake_case keys, and the fields its loader adds.
 */
@Component
class MongoCompendium(private val mongo: MongoTemplate) : Compendium {
    override fun entry(kind: Kind, index: Index): Entry? =
        mongo.findOne(Query(Criteria.where("index").`is`(index.value)), Document::class.java, collection(kind))
            ?.let { entry(it) }

    override fun entries(kind: Kind, filter: Map<String, Any>, page: PageRequest): List<Entry> {
        val query = Query()
        for ((field, value) in filter) query.addCriteria(Criteria.where(snakeCase(field)).`is`(value))
        return find(kind, query, page)
    }

    override fun entriesNamed(kind: Kind, text: String, page: PageRequest): List<Entry> =
        find(kind, Query(Criteria.where("name").regex(".*" + Pattern.quote(text) + ".*", "i")), page)

    private fun find(kind: Kind, query: Query, page: PageRequest): List<Entry> {
        query.skip(page.number.toLong() * page.size).limit(page.size)
        if (page.order.isNotEmpty()) {
            query.with(Sort.by(page.order.map { Sort.Order(if (it.descending) Sort.Direction.DESC else Sort.Direction.ASC, snakeCase(it.field)) }))
        }
        return mongo.find(query, Document::class.java, collection(kind)).map { entry(it) }
    }
}

/** Upstream's loader names a collection by edition and file: `2014-spells`. */
private fun collection(kind: Kind) = "${kind.edition}-${kind.name}"

/** Fields upstream's loader adds that are not SRD facts. */
private val STORAGE_FIELDS = setOf("_id", "updated_at")

/** A stored document in the Compendium's spelling: camelCase keys, no storage fields, no nulls. */
private fun entry(document: Document): Entry = camelCase(document - STORAGE_FIELDS)

private fun camelCase(document: Map<*, *>): MutableMap<String, Any?> {
    val result = LinkedHashMap<String, Any?>()
    for ((key, value) in document) {
        if (value != null) result[camelCase(key as String)] = camelCaseValue(value)
    }
    return result
}

private fun camelCaseValue(value: Any): Any = when (value) {
    is Map<*, *> -> camelCase(value)
    is List<*> -> value.map { if (it == null) null else camelCaseValue(it) }
    else -> value
}

private fun camelCase(key: String) = key.replace(Regex("_([a-z0-9])")) { it.groupValues[1].uppercase() }

private fun snakeCase(key: String) = key.replace(Regex("([A-Z])")) { "_" + it.groupValues[1].lowercase() }
