package com.thejaxonhill.dnd5eapi

import org.springframework.data.domain.Pageable

// Pure functions: values in, values out, no I/O. They turn upstream's stored document into what
// REST and MCP return (REQ2/SHAPE-3).

/** Fields the store adds that are not upstream's. */
private val STORAGE_FIELDS = setOf("_id", "updated_at")

/** An item as REST returns it: upstream's document with camelCase keys, no nulls, and the resource's departures. */
fun item(resource: Resource, document: Map<String, Any?>): MutableMap<String, Any?> {
    val item = camelCase(document - STORAGE_FIELDS)
    for (departure in resource.departures) depart(item, departure)
    return item
}

/** The same item as MCP returns it, with the resource's [McpNulls] added. */
fun mcpItem(resource: Resource, document: Map<String, Any?>): MutableMap<String, Any?> {
    val item = item(resource, document)
    for (nulls in resource.mcpNulls) {
        for (target in objectsAt(item, nulls.at)) {
            val where = nulls.onlyWhere
            if (where != null && target[where.first] != where.second) continue
            for (field in nulls.fields) target.putIfAbsent(field, null)
        }
    }
    return item
}

/** A paged list (REQ1/API-2); `sort` is written as `property,direction`. */
class Paged(val content: List<Map<String, Any?>>, val page: PageInfo)

class PageInfo(val page: Int, val size: Int, val sort: List<String>)

fun paged(content: List<Map<String, Any?>>, pageable: Pageable) = Paged(
    content,
    PageInfo(
        pageable.pageNumber,
        pageable.pageSize,
        pageable.sort.map { "${it.property},${it.direction.name.lowercase()}" }.toList(),
    ),
)

fun camelCase(key: String) = key.replace(Regex("_([a-z0-9])")) { it.groupValues[1].uppercase() }

fun snakeCase(key: String) = key.replace(Regex("([A-Z])")) { "_" + it.groupValues[1].lowercase() }

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

private fun depart(item: MutableMap<String, Any?>, departure: Departure) {
    val field = departure.path.substringAfterLast('.')
    for (target in objectsAt(item, departure.path.substringBeforeLast('.', ""))) {
        val value = target[field] ?: continue
        when (departure) {
            is Dropped -> target.remove(field)
            is Emptied -> target[field] = emptyList<Any>()
            is Truncated -> target[field] = (value as Number).toInt()
            is Decimal -> target[field] = (value as Number).toDouble()
            is Text -> target[field] = value.toString()
            is Uppercased -> target[field] = (value as String).uppercase()
            is Joined -> target[field] = (value as List<*>).joinToString(",")
            is Renamed -> target[departure.to] = target.remove(field)
            is Replaced -> if (value == departure.from) target[field] = departure.to
        }
    }
}

/** Every object at a dotted path of keys, walking through lists; the empty path is the item itself. */
private fun objectsAt(item: MutableMap<String, Any?>, path: String): List<MutableMap<String, Any?>> {
    var found = listOf(item)
    if (path.isEmpty()) return found
    for (key in path.split('.')) found = found.flatMap { objectsIn(it[key]) }
    return found
}

@Suppress("UNCHECKED_CAST")
private fun objectsIn(value: Any?): List<MutableMap<String, Any?>> = when (value) {
    is MutableMap<*, *> -> listOf(value as MutableMap<String, Any?>)
    is List<*> -> value.flatMap { objectsIn(it) }
    else -> emptyList()
}
