package com.thejaxonhill.dnd5eapi.progression.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.progression.domain.ClassSpecific
import com.thejaxonhill.dnd5eapi.progression.domain.SubclassSpecific
import com.thejaxonhill.dnd5eapi.shared.domain.model.ApiReference
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "2014-levels")
class LevelDocument(
    @Id
    var id: String? = null,
    var abilityScoreBonuses: Long? = null,
    @Field("class")
    var characterClass: ApiReference? = null,
    var classSpecific: ClassSpecific? = null,
    var features: List<ApiReference>? = null,
    var index: String? = null,
    var level: Int? = null,
    var profBonus: Int? = null,
    var spellcasting: SpellcastingSlotsDocument? = null,
    var subclass: ApiReference? = null,
    var subclassSpecific: SubclassSpecific? = null,
    var url: String? = null,
    var updatedAt: LocalDateTime? = null
)

// Upstream writes spell_slots_level_1; the snake-case strategy would make spell_slots_level1, which
// is absent, so the nine slot fields name their upstream keys explicitly.
class SpellcastingSlotsDocument(
    var cantripsKnown: Int? = null,
    @Field("spell_slots_level_1") var spellSlotsLevel1: Int? = null,
    @Field("spell_slots_level_2") var spellSlotsLevel2: Int? = null,
    @Field("spell_slots_level_3") var spellSlotsLevel3: Int? = null,
    @Field("spell_slots_level_4") var spellSlotsLevel4: Int? = null,
    @Field("spell_slots_level_5") var spellSlotsLevel5: Int? = null,
    @Field("spell_slots_level_6") var spellSlotsLevel6: Int? = null,
    @Field("spell_slots_level_7") var spellSlotsLevel7: Int? = null,
    @Field("spell_slots_level_8") var spellSlotsLevel8: Int? = null,
    @Field("spell_slots_level_9") var spellSlotsLevel9: Int? = null,
    var spellsKnown: Int? = null,
)