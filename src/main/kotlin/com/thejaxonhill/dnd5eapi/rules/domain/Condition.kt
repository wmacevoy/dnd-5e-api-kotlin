package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index


data class Condition(
    val index: Index,
    val name: String,
    val desc: List<String>,
    val url: String,
)

data class ConditionExample(val name: String?)
