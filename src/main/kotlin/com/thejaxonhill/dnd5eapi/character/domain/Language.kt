package com.thejaxonhill.dnd5eapi.character.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index


data class Language(
    val desc: String? = null,
    val index: Index,
    val name: String,
    val script: String? = null,
    val type: String,
    val typicalSpeakers: List<String>,
    val url: String,
)

data class LanguageExample(val name: String?)
