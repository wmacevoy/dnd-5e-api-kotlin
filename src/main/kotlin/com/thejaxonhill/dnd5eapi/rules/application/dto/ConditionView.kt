package com.thejaxonhill.dnd5eapi.rules.application.dto

import com.thejaxonhill.dnd5eapi.rules.domain.Condition

data class ConditionView(
    val index: String,
    val name: String,
    val desc: List<String>,
    val url: String,
)

fun Condition.toView() = ConditionView(
    index = index.value,
    name = name,
    desc = desc,
    url = url,
)
