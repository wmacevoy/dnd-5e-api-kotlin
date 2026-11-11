package com.thejaxonhill.dnd5eapi.shared.domain.model

/**
 * An item's identifier, unique within its resource: lowercase letters, digits, and hyphens, with a
 * letter or digit at each end. Upstream sometimes runs hyphens together
 * (`dragon-ancestor-black---acid-damage`), so the rule allows that.
 */
@JvmInline
value class Index private constructor(val value: String) {
    companion object {
        private val FORM = Regex("[a-z0-9]+(-+[a-z0-9]+)*")

        /** Null when [text] cannot be an index, so a lookup by it finds nothing (REQ1/API-4). */
        fun parse(text: String): Index? = if (FORM.matches(text)) Index(text) else null

        /** For text that must already be an index, such as an upstream document's. */
        fun of(text: String): Index = parse(text) ?: throw IllegalArgumentException("Not an index: '$text'")
    }
}
