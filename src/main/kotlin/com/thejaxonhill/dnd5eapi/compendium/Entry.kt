package com.thejaxonhill.dnd5eapi.compendium

/**
 * One SRD entry, as upstream wrote it, with its keys in camelCase. The Compendium conforms to
 * upstream (REQ2/SHAPE-4), so an entry has no type of its own: a type is earned by a rule that reads
 * its fields, and none does yet (REQ2/SHAPE-5).
 */
typealias Entry = Map<String, Any?>

/**
 * An entry's identity within its kind: lowercase letters, digits, and hyphens, with a letter or digit
 * at each end. Upstream sometimes runs hyphens together (`dragon-ancestor-black---acid-damage`).
 */
@JvmInline
value class Index private constructor(val value: String) {
    companion object {
        private val FORM = Regex("[a-z0-9]+(-+[a-z0-9]+)*")

        /** Null when [text] cannot be an index, so a lookup by it finds nothing (REQ1/API-4). */
        fun parse(text: String): Index? = if (FORM.matches(text)) Index(text) else null
    }
}

/** Which page of a list to return: numbered from 0, with the fields to order by, in order. */
class PageRequest(val number: Int, val size: Int, val order: List<Order> = emptyList()) {
    init {
        // The same rules, and words, as the paging library this API used before.
        require(number >= 0) { "Page index must not be less than zero" }
        require(size >= 1) { "Page size must not be less than one" }
    }
}

class Order(val field: String, val descending: Boolean = false)

/** A page of entries, and the request it answers. */
class EntryPage(val entries: List<Entry>, val request: PageRequest)
