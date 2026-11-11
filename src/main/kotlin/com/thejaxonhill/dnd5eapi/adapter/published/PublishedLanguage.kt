package com.thejaxonhill.dnd5eapi.adapter.published

import com.thejaxonhill.dnd5eapi.compendium.Entry
import com.thejaxonhill.dnd5eapi.compendium.EntryPage
import com.thejaxonhill.dnd5eapi.compendium.Kind

// The open host service's published language (REQ2/SHAPE-4): what REST and MCP clients receive.
// It is an entry as the Compendium has it, except where today's output departs from upstream. Clients
// depend on that output, so each departure is kept until changing it is decided with them
// (EXPERIMENT.md, rule 3). Most of them lose data, so each is a candidate defect.

/** An entry as clients receive it. */
fun published(kind: Kind, entry: Entry): MutableMap<String, Any?> {
    val result = copy(entry)
    for (departure in DEPARTURES[kind.name].orEmpty()) depart(result, departure)
    return result
}

/** A paged list (REQ1/API-2); `sort` is written as `property,direction`. */
class Paged(val content: List<Map<String, Any?>>, val page: PageInfo)

class PageInfo(val page: Int, val size: Int, val sort: List<String>)

fun paged(page: EntryPage, item: (Entry) -> Map<String, Any?>) = Paged(
    page.entries.map(item),
    PageInfo(
        page.request.number,
        page.request.size,
        page.request.order.map { "${it.field},${if (it.descending) "desc" else "asc"}" },
    ),
)

/**
 * One way today's output departs from upstream's entry. A path names a field by the keys leading to
 * it; lists along the way are walked through, so `actions.usage.times` is `times` in the `usage` of
 * every action.
 */
sealed interface Departure {
    val path: String
}

/** The field is left out. */
class Dropped(override val path: String) : Departure

/** A list is replaced by an empty one. */
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

/** By kind. Found by diffing upstream's JSON against the recorded output; 17 kinds have none. */
private val DEPARTURES: Map<String, List<Departure>> = mapOf(
    "backgrounds" to listOf(Replaced("ideals.from.options.optionType", "ideal", "ideal-option")),
    "classes" to listOf(Emptied("startingEquipmentOptions.from.options.items")),
    "equipment" to listOf(Dropped("image"), Joined("desc"), Truncated("speed.quantity"), Decimal("weight")),
    "features" to listOf(
        Renamed("class", "characterClass"),
        Dropped("prerequisites"),
        Dropped("featureSpecific.enemyTypeOptions"),
        Dropped("featureSpecific.terrainTypeOptions"),
        Emptied("featureSpecific.expertiseOptions.from.options.items"),
    ),
    "levels" to listOf(
        Truncated("classSpecific.destroyUndeadCr"),
        Truncated("classSpecific.wildShapeMaxCr"),
        // Upstream writes spell_slots_level_1; the old reader looked for spell_slots_level1.
        Dropped("classSpecific.magicalSecretsMax5"),
        Dropped("classSpecific.magicalSecretsMax7"),
        Dropped("classSpecific.magicalSecretsMax9"),
        Dropped("classSpecific.mysticArcanumLevel6"),
        Dropped("classSpecific.mysticArcanumLevel7"),
        Dropped("classSpecific.mysticArcanumLevel8"),
        Dropped("classSpecific.mysticArcanumLevel9"),
    ) + (1..9).map { Dropped("spellcasting.spellSlotsLevel$it") },
    "monsters" to listOf(
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
)

private fun depart(item: MutableMap<String, Any?>, departure: Departure) {
    val field = departure.path.substringAfterLast('.')
    for (target in objectsAt(item, departure.path.substringBeforeLast('.', ""))) {
        val value = target[field] ?: continue
        when (departure) {
            is Dropped -> target.remove(field)
            is Emptied -> target[field] = emptyList<Any>()
            is Truncated -> target[field] = (value as Number).toInt()
            is Decimal -> target[field] = (value as Number).toDouble()
            is Text -> target[field] = value.toString()
            is Uppercased -> target[field] = (value as String).uppercase()
            is Joined -> target[field] = (value as List<*>).joinToString(",")
            is Renamed -> target[departure.to] = target.remove(field)
            is Replaced -> if (value == departure.from) target[field] = departure.to
        }
    }
}

/** Every object at a dotted path of keys, walking through lists; the empty path is the item itself. */
internal fun objectsAt(item: MutableMap<String, Any?>, path: String): List<MutableMap<String, Any?>> {
    var found = listOf(item)
    if (path.isEmpty()) return found
    for (key in path.split('.')) found = found.flatMap { objectsIn(it[key]) }
    return found
}

@Suppress("UNCHECKED_CAST")
private fun objectsIn(value: Any?): List<MutableMap<String, Any?>> = when (value) {
    is MutableMap<*, *> -> listOf(value as MutableMap<String, Any?>)
    is List<*> -> value.flatMap { objectsIn(it) }
    else -> emptyList()
}

/** A deep copy, so a departure never changes the entry it was given. */
private fun copy(entry: Map<*, *>): MutableMap<String, Any?> {
    val result = LinkedHashMap<String, Any?>()
    for ((key, value) in entry) result[key as String] = copyValue(value)
    return result
}

private fun copyValue(value: Any?): Any? = when (value) {
    is Map<*, *> -> copy(value)
    is List<*> -> value.map { copyValue(it) }
    else -> value
}
