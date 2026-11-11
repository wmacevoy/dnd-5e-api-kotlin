package com.thejaxonhill.dnd5eapi.character.application.dto

import com.thejaxonhill.dnd5eapi.character.domain.Alignment

data class AlignmentView(
    val index: String,
    val name: String? = null,
    val abbreviation: String? = null,
    val desc: String? = null,
    val url: String? = null,
)

fun Alignment.toView() = AlignmentView(
    index = index,
    name = name,
    abbreviation = abbreviation,
    desc = desc,
    url = url,
)
