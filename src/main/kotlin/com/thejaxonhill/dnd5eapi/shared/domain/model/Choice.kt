package com.thejaxonhill.dnd5eapi.shared.domain.model

data class Choice(
    val desc: String? = null,
    val choose: Int,
    val type: String,
    val from: OptionSet
)
