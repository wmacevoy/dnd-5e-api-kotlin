package com.thejaxonhill.dnd5eapi.adapter.mcp

/**
 * MCP's published language carries a `null` for each field the old types declared that an entry
 * lacks, where REST leaves it out (REQ1/API-7 covers REST only). [at] is a dotted path of keys, empty
 * for the entry itself, walking through lists. Where [onlyWhere] is given, only objects with that
 * field and value get the nulls.
 */
class McpNulls(val at: String, vararg fields: String, val onlyWhere: Pair<String, String>? = null) {
    val fields = fields.toList()
}

/** By kind. Found by reading the recorded MCP output; kinds not listed have none. */
val MCP_NULLS: Map<String, List<McpNulls>> = mapOf(
    "backgrounds" to listOf(
        McpNulls("bonds", "desc"),
        McpNulls("flaws", "desc"),
        McpNulls("ideals", "desc"),
        McpNulls("languageOptions", "desc"),
        McpNulls("personalityTraits", "desc"),
        McpNulls("startingEquipmentOptions", "desc"),
    ),
    "classes" to listOf(
        McpNulls("", "spellcasting", "spells"),
        McpNulls("multiClassing", "prerequisiteOptions", "prerequisites", "proficiencyChoices"),
        McpNulls("multiClassing.proficiencyChoices", "desc"),
        McpNulls("multiClassing.prerequisiteOptions", "desc"),
        McpNulls(
            "startingEquipmentOptions.from.options", "prerequisites",
            onlyWhere = "optionType" to "counted_reference",
        ),
    ),
    "equipment" to listOf(
        McpNulls(
            "", "armorCategory", "armorClass", "capacity", "categoryRange", "contents", "damage", "desc",
            "gearCategory", "properties", "quantity", "range", "special", "speed", "stealthDisadvantage",
            "strMinimum", "throwRange", "toolCategory", "twoHandedDamage", "vehicleCategory",
            "weaponCategory", "weaponRange", "weight",
        ),
        McpNulls("range", "long"),
        McpNulls("armorClass", "maxBonus"),
    ),
    "features" to listOf(
        McpNulls("", "featureSpecific", "parent", "prerequisite", "reference", "subclass"),
        McpNulls("featureSpecific", "expertiseOptions", "invocations", "subfeatureOptions"),
        McpNulls("featureSpecific.expertiseOptions", "desc"),
        McpNulls("featureSpecific.subfeatureOptions", "desc"),
        McpNulls("featureSpecific.expertiseOptions.from.options.choice", "desc"),
    ),
    "languages" to listOf(McpNulls("", "desc", "script")),
    "levels" to listOf(
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
    "magic-items" to listOf(McpNulls("", "image")),
    "monsters" to listOf(
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
    "races" to listOf(
        McpNulls(
            "", "abilityBonusOptions", "languageOptions", "startingProficiencies",
            "startingProficiencyOptions",
        ),
        McpNulls("languageOptions", "desc"),
        McpNulls("abilityBonusOptions", "desc"),
    ),
    "spells" to listOf(
        McpNulls("", "areaOfEffect", "attackType", "damage", "dc", "healAtSlotLevel", "higherLevel", "material"),
        McpNulls("damage", "damageAtCharacterLevel", "damageAtSlotLevel", "damageType"),
        McpNulls("dc", "desc"),
    ),
    "subclasses" to listOf(McpNulls("", "spells")),
    "subraces" to listOf(McpNulls("", "languageOptions", "languages", "startingProficiencies")),
    "traits" to listOf(
        McpNulls("", "languageOptions", "parent", "proficiencyChoices", "traitSpecific"),
        McpNulls("languageOptions", "desc"),
        McpNulls("proficiencyChoices", "desc"),
        McpNulls("traitSpecific", "breathWeapon", "damageType", "spellOptions", "subtraitOptions"),
        McpNulls("traitSpecific.spellOptions", "desc"),
        McpNulls("traitSpecific.subtraitOptions", "desc"),
        McpNulls("traitSpecific.breathWeapon.dc", "dcValue"),
    ),
)
