package com.thejaxonhill.dnd5eapi.compendium

/**
 * The SRD as loaded: the Compendium's one port to storage (REQ2/SHAPE-6). Upstream owns the data,
 * and this only reads it (REQ2/SHAPE-5). Entries come back in stored order unless a page orders them.
 */
interface Compendium {
    fun entry(kind: Kind, index: Index): Entry?

    /** Entries whose fields equal every value in [filter]. */
    fun entries(kind: Kind, filter: Map<String, Any>, page: PageRequest): List<Entry>

    /** Entries whose name contains [text], ignoring case. */
    fun entriesNamed(kind: Kind, text: String, page: PageRequest): List<Entry>
}

/**
 * The questions clients may ask the Compendium. REST and MCP both ask through here, so the same
 * question gets the same answer on each (REQ2/SHAPE-6).
 */
class CompendiumQueries(private val compendium: Compendium) {
    /** The entry, or `NoSuchElementException` when there is none, including for text that cannot be an index. */
    fun entry(kind: Kind, index: String): Entry =
        Index.parse(index)?.let { compendium.entry(kind, it) } ?: throw NoSuchElementException()

    fun entries(kind: Kind, filter: Map<String, Any>, page: PageRequest) =
        EntryPage(compendium.entries(kind, filter, page), page)

    fun entriesNamed(kind: Kind, text: String, page: PageRequest): EntryPage {
        require(kind.searchableByName) { "${kind.plural} cannot be searched by name" }
        return EntryPage(compendium.entriesNamed(kind, text, page), page)
    }
}
