package com.thejaxonhill.dnd5eapi

/**
 * One resource this API serves. REST routes, MCP tools, and the OpenAPI document are all generated
 * from these rows, so the next resource is one more row (REQ2/SHAPE-1).
 */
class Resource(
    /** The path under `/api`. */
    val path: String,
    val collection: String,
    /** Swagger's tag, and the nouns in its summaries: "Get a paginated list of [plural]", "Get [singular] by index". */
    val tag: String,
    val plural: String,
    val singular: String,
    val listTool: String,
    val getTool: String,
    /** Fields a list request may filter on, matched exactly against the stored item. */
    val filters: List<Filter> = listOf(Filter("name")),
    val listArguments: ListArguments = PageNumberAndSize,
    /** Equipment's REST list sorts by name with 10 per page, where the others are unsorted with 20 (REQ1/API-5). */
    val listedByName: Boolean = false,
    /** Only equipment has `/search` (REQ1/API-5). */
    val searchable: Boolean = false,
    val departures: List<Departure> = emptyList(),
    val mcpNulls: List<McpNulls> = emptyList(),
)

class Filter(val name: String, val isInteger: Boolean = false)

/** How an MCP list tool takes its arguments. There are two shapes today (queue item 11). */
sealed interface ListArguments

/** `pageNumber` (default 1, so the first page is skipped) and `pageSize` (default 20); no filters. */
data object PageNumberAndSize : ListArguments

/** A required `page` object, and an optional object of filters under [exampleName]. */
class PageAndExample(val exampleName: String) : ListArguments

/**
 * One way the old typed code's output departed from upstream's document, kept so that the output
 * does not change (EXPERIMENT.md, rule 3). Most of them lose data, so each is a candidate defect.
 *
 * A path names a field by the keys leading to it, in camelCase; lists along the way are walked
 * through, so `actions.usage.times` is `times` in the `usage` of every action.
 */
sealed interface Departure {
    val path: String
}

/** The field is left out: the old types did not declare it, or declared it under another name. */
class Dropped(override val path: String) : Departure

/** A list is replaced by an empty one: the old reader looked for its items under another key. */
class Emptied(override val path: String) : Departure

/** A number loses its fraction: 0.5 becomes 0. */
class Truncated(override val path: String) : Departure

/** A whole number is written as a decimal: 2 becomes 2.0. */
class Decimal(override val path: String) : Departure

/** A number is written as a string: 2 becomes "2". */
class Text(override val path: String) : Departure

class Uppercased(override val path: String) : Departure

/** A list of strings becomes one string, joined with commas and no spaces. */
class Joined(override val path: String) : Departure

class Renamed(override val path: String, val to: String) : Departure

class Replaced(override val path: String, val from: String, val to: String) : Departure

/**
 * MCP results carry a `null` for each field the old types declared that an item lacks, where REST
 * leaves it out (REQ1/API-7 covers REST only). [at] is a path as in [Departure], and empty for the
 * item itself. Where [onlyWhere] is given, only objects with that field and value get the nulls.
 */
class McpNulls(val at: String, vararg fields: String, val onlyWhere: Pair<String, String>? = null) {
    val fields = fields.toList()
}

val RESOURCES = listOf(
    Resource(
        "ability-scores", "2014-ability-scores", "Ability Scores", "ability scores", "an ability score",
        "getAbilityScores", "getAbilityScore",
        listArguments = PageAndExample("abilityScoreExample"),
    ),
    Resource(
        "alignments", "2014-alignments", "Alignments", "alignments", "an alignment",
        "getAlignments", "getAlignment",
        filters = listOf(Filter("name"), Filter("abbreviation")),
        listArguments = PageAndExample("example"),
    ),
    Resource(
        "backgrounds", "2014-backgrounds", "Backgrounds", "backgrounds", "a background",
        "getBackgrounds", "getBackground",
        departures = listOf(Replaced("ideals.from.options.optionType", "ideal", "ideal-option")),
        mcpNulls = listOf(
            McpNulls("bonds", "desc"),
            McpNulls("flaws", "desc"),
            McpNulls("ideals", "desc"),
            McpNulls("languageOptions", "desc"),
            McpNulls("personalityTraits", "desc"),
            McpNulls("startingEquipmentOptions", "desc"),
        ),
    ),
    Resource(
        "classes", "2014-classes", "Character Classes", "character classes", "a character class",
        "getClasses", "getCharacterClass",
        departures = listOf(Emptied("startingEquipmentOptions.from.options.items")),
        mcpNulls = listOf(
            McpNulls("", "spellcasting", "spells"),
            McpNulls("multiClassing", "prerequisiteOptions", "prerequisites", "proficiencyChoices"),
            McpNulls("multiClassing.proficiencyChoices", "desc"),
            McpNulls("multiClassing.prerequisiteOptions", "desc"),
            McpNulls(
                "startingEquipmentOptions.from.options", "prerequisites",
                onlyWhere = "optionType" to "counted_reference",
            ),
        ),
    ),
    Resource(
        "conditions", "2014-conditions", "Conditions", "conditions", "a condition",
        "getConditions", "getCondition",
    ),
    Resource(
        "damage-types", "2014-damage-types", "Damage Types", "damage types", "a damage type",
        "getDamageTypes", "getDamageType",
    ),
    Resource(
        "equipment", "2014-equipment", "Equipment", "equipment", "equipment",
        "getAllEquipment", "getEquipment",
        listedByName = true,
        searchable = true,
        departures = listOf(Dropped("image"), Joined("desc"), Truncated("speed.quantity"), Decimal("weight")),
        mcpNulls = listOf(
            McpNulls(
                "", "armorCategory", "armorClass", "capacity", "categoryRange", "contents", "damage", "desc",
                "gearCategory", "properties", "quantity", "range", "special", "speed", "stealthDisadvantage",
                "strMinimum", "throwRange", "toolCategory", "twoHandedDamage", "vehicleCategory",
                "weaponCategory", "weaponRange", "weight",
            ),
            McpNulls("range", "long"),
            McpNulls("armorClass", "maxBonus"),
        ),
    ),
    Resource(
        "equipment-categories", "2014-equipment-categories", "Equipment Categories", "equipment categories",
        "an equipment category", "getEquipmentCategories", "getEquipmentCategory",
    ),
    Resource(
        "feats", "2014-feats", "Feats", "feats", "a feat",
        "getFeats", "getFeat",
    ),
    Resource(
        "features", "2014-features", "Features", "features", "a feature",
        "getFeatures", "getFeature",
        departures = listOf(
            Renamed("class", "characterClass"),
            Dropped("prerequisites"),
            Dropped("featureSpecific.enemyTypeOptions"),
            Dropped("featureSpecific.terrainTypeOptions"),
            Emptied("featureSpecific.expertiseOptions.from.options.items"),
        ),
        mcpNulls = listOf(
            McpNulls("", "featureSpecific", "parent", "prerequisite", "reference", "subclass"),
            McpNulls("featureSpecific", "expertiseOptions", "invocations", "subfeatureOptions"),
            McpNulls("featureSpecific.expertiseOptions", "desc"),
            McpNulls("featureSpecific.subfeatureOptions", "desc"),
            McpNulls("featureSpecific.expertiseOptions.from.options.choice", "desc"),
        ),
    ),
    Resource(
        "languages", "2014-languages", "Languages", "languages", "a language",
        "getLanguages", "getLanguage",
        mcpNulls = listOf(McpNulls("", "desc", "script")),
    ),
    Resource(
        "levels", "2014-levels", "Levels", "levels", "a level",
        "getLevels", "getLevel",
        filters = listOf(Filter("level", isInteger = true)),
        departures = listOf(
            Truncated("classSpecific.destroyUndeadCr"),
            Truncated("classSpecific.wildShapeMaxCr"),
            // Upstream writes spell_slots_level_1; the old names read spell_slots_level1, so never matched.
        ) + (1..9).map { Dropped("spellcasting.spellSlotsLevel$it") },
        mcpNulls = listOf(
            McpNulls(
                "", "abilityScoreBonuses", "classSpecific", "profBonus", "spellcasting", "subclass",
                "subclassSpecific",
            ),
            McpNulls(
                "classSpecific", "actionSurges", "arcaneRecoveryLevels", "auraRange", "bardicInspirationDie",
                "brutalCriticalDice", "channelDivinityCharges", "creatingSpellSlots", "destroyUndeadCr",
                "extraAttacks", "favoredEnemies", "favoredTerrain", "indomitableUses", "invocationsKnown",
                "kiPoints", "magicalSecretsMax5", "magicalSecretsMax7", "magicalSecretsMax9", "martialArts",
                "metamagicKnown", "mysticArcanumLevel6", "mysticArcanumLevel7", "mysticArcanumLevel8",
                "mysticArcanumLevel9", "rageCount", "rageDamageBonus", "sneakAttack", "songOfRestDie",
                "sorceryPoints", "unarmoredMovement", "wildShapeFly", "wildShapeMaxCr", "wildShapeSwim",
            ),
            McpNulls(
                "spellcasting", "cantripsKnown", "spellSlotsLevel1", "spellSlotsLevel2", "spellSlotsLevel3",
                "spellSlotsLevel4", "spellSlotsLevel5", "spellSlotsLevel6", "spellSlotsLevel7",
                "spellSlotsLevel8", "spellSlotsLevel9", "spellsKnown",
            ),
            McpNulls("subclassSpecific", "additionalMagicalSecretsMaxLvl", "auraRange"),
        ),
    ),
    Resource(
        "magic-items", "2014-magic-items", "Magic Items", "magic items", "a magic item",
        "getMagicItems", "getMagicItem",
        mcpNulls = listOf(McpNulls("", "image")),
    ),
    Resource(
        "magic-schools", "2014-magic-schools", "Magic Schools", "magic schools", "a magic school",
        "getMagicSchools", "getMagicSchool",
    ),
    Resource(
        "monsters", "2014-monsters", "Monsters", "monsters", "a monster",
        "getMonsters", "getMonster",
        departures = listOf(
            Dropped("desc"),
            Dropped("proficiencyBonus"),
            Decimal("challengeRating"),
            Uppercased("armorClass.type"),
            Dropped("actions.attacks"),
            Text("actions.actions.count"),
            // A damage entry that is a choice (choose, from, type) comes out empty.
            Dropped("actions.damage.choose"),
            Dropped("actions.damage.from"),
            Dropped("actions.damage.type"),
            Dropped("actions.damage.dc"),
            Dropped("actions.usage.restTypes"),
            Dropped("actions.usage.times"),
            Dropped("actions.actionOptions.from.options.desc"),
            Emptied("actions.actionOptions.from.options.items"),
            // The old reader looked for a breath option's `dc` under `dt`.
            Dropped("actions.options.from.options.dc"),
        ),
        mcpNulls = listOf(
            McpNulls("", "actions", "forms", "legendaryActions", "reactions", "specialAbilities", "subtype"),
            McpNulls("armorClass", "desc"),
            McpNulls("armorClass", "armor", onlyWhere = "type" to "ARMOR"),
            McpNulls(
                "actions", "actionOptions", "actions", "attackBonus", "damage", "dc", "multiattackType", "options",
                "usage",
            ),
            McpNulls("actions.damage", "damageDice", "damageType"),
            McpNulls("actions.usage", "dice", "minValue"),
            McpNulls("actions.options", "desc"),
            McpNulls("actions.options.from.options", "damage", "dt"),
            McpNulls("actions.actionOptions", "desc"),
            McpNulls("actions.actionOptions.from.options", "notes", onlyWhere = "optionType" to "action"),
            McpNulls("legendaryActions", "attackBonus", "damage", "dc"),
            McpNulls("reactions", "dc"),
            McpNulls("senses", "blindsight", "darkvision", "tremorsense", "truesight"),
            McpNulls("speed", "burrow", "climb", "fly", "hover", "swim", "walk"),
            McpNulls("specialAbilities", "attackBonus", "damage", "dc", "spellcasting", "usage"),
            McpNulls("specialAbilities.usage", "restTypes", "times"),
            McpNulls("specialAbilities.spellcasting", "dc", "level", "modifier", "school", "slots"),
            McpNulls("specialAbilities.spellcasting.spells", "notes", "usage"),
            McpNulls("specialAbilities.spellcasting.spells.usage", "restTypes", "times"),
        ),
    ),
    Resource(
        "proficiencies", "2014-proficiencies", "Proficiencies", "proficiencies", "a proficiency",
        "getProficiencies", "getProficiency",
    ),
    Resource(
        "races", "2014-races", "Races", "races", "a race",
        "getRaces", "getRace",
        mcpNulls = listOf(
            McpNulls(
                "", "abilityBonusOptions", "languageOptions", "startingProficiencies",
                "startingProficiencyOptions",
            ),
            McpNulls("languageOptions", "desc"),
            McpNulls("abilityBonusOptions", "desc"),
        ),
    ),
    Resource(
        "rule-sections", "2014-rule-sections", "Rule Sections", "rule sections", "a rule section",
        "getRuleSections", "getRuleSection",
    ),
    Resource(
        "rules", "2014-rules", "Rules", "rules", "a rule",
        "getRules", "getRule",
    ),
    Resource(
        "skills", "2014-skills", "Skills", "skills", "a skill",
        "getSkills", "getSkill",
    ),
    Resource(
        "spells", "2014-spells", "Spells", "spells", "a spell",
        "getSpells", "getSpell",
        mcpNulls = listOf(
            McpNulls("", "areaOfEffect", "attackType", "damage", "dc", "healAtSlotLevel", "higherLevel", "material"),
            McpNulls("damage", "damageAtCharacterLevel", "damageAtSlotLevel", "damageType"),
            McpNulls("dc", "desc"),
        ),
    ),
    Resource(
        "subclasses", "2014-subclasses", "Subclasses", "subclasses", "a subclass",
        "getSubclasses", "getSubclass",
        mcpNulls = listOf(McpNulls("", "spells")),
    ),
    Resource(
        "subraces", "2014-subraces", "Subraces", "subraces", "a subrace",
        "getSubraces", "getSubrace",
        mcpNulls = listOf(McpNulls("", "languageOptions", "languages", "startingProficiencies")),
    ),
    Resource(
        "traits", "2014-traits", "Traits", "traits", "a trait",
        "getTraits", "getTrait",
        mcpNulls = listOf(
            McpNulls("", "languageOptions", "parent", "proficiencyChoices", "traitSpecific"),
            McpNulls("languageOptions", "desc"),
            McpNulls("proficiencyChoices", "desc"),
            McpNulls("traitSpecific", "breathWeapon", "damageType", "spellOptions", "subtraitOptions"),
            McpNulls("traitSpecific.spellOptions", "desc"),
            McpNulls("traitSpecific.subtraitOptions", "desc"),
            McpNulls("traitSpecific.breathWeapon.dc", "dcValue"),
        ),
    ),
    Resource(
        "weapon-properties", "2014-weapon-properties", "Weapon Properties", "weapon properties",
        "a weapon property", "getWeaponProperties", "getWeaponProperty",
    ),
    // From the 2024 SRD; its items' `url`s say /api/2024/, as the 2014 ones say /api/2014/.
    Resource(
        "weapon-mastery-properties", "2024-weapon-mastery-properties", "Weapon Mastery Properties",
        "weapon mastery properties", "a weapon mastery property",
        "getWeaponMasteryProperties", "getWeaponMasteryProperty",
    ),
)
