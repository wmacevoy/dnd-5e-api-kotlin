package com.thejaxonhill.dnd5eapi.progression.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.fasterxml.jackson.annotation.JsonProperty
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference

data class Subclass(
    @get:JsonProperty("class")
    val characterClass: ApiReference,
    val desc: List<String>,
    val index: Index,
    val name: String,
    val spells: List<Spell>? = null,
    val subclassFlavor: String,
    val subclassLevels: String,
    val url: String,
)

data class SubclassExample(val name: String?)
data class Spell(
    val prerequisites: List<SpellPrerequisite>,
    val spell: ApiReference,
)

data class SpellPrerequisite(
    val index: Index,
    val name: String,
    val type: String,
    val url: String,
)