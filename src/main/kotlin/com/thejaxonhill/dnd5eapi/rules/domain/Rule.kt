package com.thejaxonhill.dnd5eapi.rules.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference

data class Rule(
    val desc: String,
    val index: String,
    val name: String,
    val subsections: List<ApiReference>,
    val url: String,
)

data class RuleExample(val name: String?)
