package com.thejaxonhill.dnd5eapi.progression.domain

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference
import com.thejaxonhill.dnd5eapi.shared.domain.model.Choice
import com.thejaxonhill.dnd5eapi.shared.domain.model.Prerequisite

data class Feature(
    val characterClass: ApiReference,
    val desc: List<String>,
    val parent: ApiReference? = null,
    val index: Index,
    val level: Int,
    val name: String,
    val prerequisite: List<Prerequisite>?,
    val reference: String?,
    val subclass: ApiReference?,
    val featureSpecific: FeatureSpecific?,
    val url: String,
)

data class FeatureExample(val name: String?)
data class FeatureSpecific(
    val subfeatureOptions: Choice?,
    val expertiseOptions: Choice?,
    val invocations: List<ApiReference>?
)