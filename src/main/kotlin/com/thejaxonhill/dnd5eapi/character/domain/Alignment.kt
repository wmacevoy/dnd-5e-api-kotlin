package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index


class Alignment(
    val index: Index,
    val name: String? = null,
    val abbreviation: String? = null,
    val desc: String? = null,
    val url: String? = null,
)

data class AlignmentExample(val name: String?, val abbreviation: String?)
