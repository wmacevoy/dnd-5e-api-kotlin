package com.thejaxonhill.dnd5eapi.progression.application.dto

import com.thejaxonhill.dnd5eapi.progression.domain.MultiClassingCheck
import com.thejaxonhill.dnd5eapi.progression.domain.UnmetPrerequisite

data class MultiClassingCheckView(
    val index: String,
    val meetsPrerequisites: Boolean,
    val unmetPrerequisites: List<UnmetPrerequisite>,
)

fun MultiClassingCheck.toView() = MultiClassingCheckView(
    index = index.value,
    meetsPrerequisites = meetsPrerequisites,
    unmetPrerequisites = unmetPrerequisites,
)
