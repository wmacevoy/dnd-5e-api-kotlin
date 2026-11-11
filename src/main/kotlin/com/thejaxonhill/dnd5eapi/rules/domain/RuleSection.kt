package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index

data class RuleSection(
    val desc: String,
    val index: Index,
    val name: String,
    val url: String,
)

data class RuleSectionExample(val name: String?)
