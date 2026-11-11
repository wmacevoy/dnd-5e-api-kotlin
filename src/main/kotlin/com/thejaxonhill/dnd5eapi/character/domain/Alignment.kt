package com.thejaxonhill.dnd5eapi.character.domain


class Alignment(
    val index: String,
    val name: String? = null,
    val abbreviation: String? = null,
    val desc: String? = null,
    val url: String? = null,
)

data class AlignmentExample(val name: String?, val abbreviation: String?)
