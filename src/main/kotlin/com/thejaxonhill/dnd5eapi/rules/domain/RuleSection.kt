package com.thejaxonhill.dnd5eapi.rules.domain

data class RuleSection(
    val desc: String,
    val index: String,
    val name: String,
    val url: String,
)

data class RuleSectionExample(val name: String?)
