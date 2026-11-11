package com.thejaxonhill.dnd5eapi.rules.domain


data class Condition(
    val index: String,
    val name: String,
    val desc: List<String>,
    val url: String,
)

data class ConditionExample(val name: String?)
