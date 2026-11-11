package com.thejaxonhill.dnd5eapi.compendium

/**
 * A kind of SRD entry: spells, monsters, and the rest (REQ2/SHAPE-3). The list of kinds is data, so
 * a new kind is one more entry in [KINDS]; storage and both surfaces follow from it.
 */
class Kind(
    /** Upstream's name for the kind, such as "spells" or "classes". */
    val name: String,
    /** The SRD's noun for one entry and for several, such as "character class". */
    val singular: String,
    val plural: String,
    /** "a" or "an" before [singular], or "" where the noun takes none ("equipment"). */
    val article: String,
    /** The fields a list may be filtered by, matched exactly. */
    val filters: List<Filter> = listOf(Filter("name")),
    /** Entries of this kind can be found by part of their name (REQ1/API-5). */
    val searchableByName: Boolean = false,
)

class Filter(val field: String, val isInteger: Boolean = false)

val KINDS = listOf(
    Kind("ability-scores", "ability score", "ability scores", "an"),
    Kind("alignments", "alignment", "alignments", "an", filters = listOf(Filter("name"), Filter("abbreviation"))),
    Kind("backgrounds", "background", "backgrounds", "a"),
    Kind("classes", "character class", "character classes", "a"),
    Kind("conditions", "condition", "conditions", "a"),
    Kind("damage-types", "damage type", "damage types", "a"),
    Kind("equipment", "equipment", "equipment", "", searchableByName = true),
    Kind("equipment-categories", "equipment category", "equipment categories", "an"),
    Kind("feats", "feat", "feats", "a"),
    Kind("features", "feature", "features", "a"),
    Kind("languages", "language", "languages", "a"),
    Kind("levels", "level", "levels", "a", filters = listOf(Filter("level", isInteger = true))),
    Kind("magic-items", "magic item", "magic items", "a"),
    Kind("magic-schools", "magic school", "magic schools", "a"),
    Kind("monsters", "monster", "monsters", "a"),
    Kind("proficiencies", "proficiency", "proficiencies", "a"),
    Kind("races", "race", "races", "a"),
    Kind("rule-sections", "rule section", "rule sections", "a"),
    Kind("rules", "rule", "rules", "a"),
    Kind("skills", "skill", "skills", "a"),
    Kind("spells", "spell", "spells", "a"),
    Kind("subclasses", "subclass", "subclasses", "a"),
    Kind("subraces", "subrace", "subraces", "a"),
    Kind("traits", "trait", "traits", "a"),
    Kind("weapon-properties", "weapon property", "weapon properties", "a"),
)
