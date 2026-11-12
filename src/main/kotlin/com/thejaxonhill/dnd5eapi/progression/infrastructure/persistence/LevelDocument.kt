package com.thejaxonhill.dnd5eapi.progression.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.progression.domain.ClassSpecificCreatingSpellSlot
import com.thejaxonhill.dnd5eapi.progression.domain.ClassSpecificMartialArt
import com.thejaxonhill.dnd5eapi.progression.domain.ClassSpecificSneakAttack
import com.thejaxonhill.dnd5eapi.progression.domain.SpellcastingSlots
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
    var classSpecific: ClassSpecificDocument? = null,
    var features: List<ApiReference>? = null,
    var index: String? = null,
    var level: Int? = null,
    var profBonus: Int? = null,
    var spellcasting: SpellcastingSlots? = null,
    var subclass: ApiReference? = null,
    var subclassSpecific: SubclassSpecific? = null,
    var url: String? = null,
    var updatedAt: LocalDateTime? = null
)

// Mirrors the domain's ClassSpecific. Upstream spells a number after an underscore
// (magical_secrets_max_5), which the snake-case strategy does not produce from magicalSecretsMax5,
// so those seven names are given here with @Field.
class ClassSpecificDocument(
    var actionSurges: Int? = null,
    var arcaneRecoveryLevels: Int? = null,
    var auraRange: Long? = null,
    var bardicInspirationDie: Int? = null,
    var brutalCriticalDice: Int? = null,
    var channelDivinityCharges: Int? = null,
    var creatingSpellSlots: List<ClassSpecificCreatingSpellSlot>? = null,
    var destroyUndeadCr: Int? = null,
    var extraAttacks: Int? = null,
    var favoredEnemies: Int? = null,
    var favoredTerrain: Int? = null,
    var indomitableUses: Int? = null,
    var invocationsKnown: Int? = null,
    var kiPoints: Int? = null,
    @Field("magical_secrets_max_5")
    var magicalSecretsMax5: Int? = null,
    @Field("magical_secrets_max_7")
    var magicalSecretsMax7: Int? = null,
    @Field("magical_secrets_max_9")
    var magicalSecretsMax9: Int? = null,
    var martialArts: ClassSpecificMartialArt? = null,
    var metamagicKnown: Int? = null,
    @Field("mystic_arcanum_level_6")
    var mysticArcanumLevel6: Int? = null,
    @Field("mystic_arcanum_level_7")
    var mysticArcanumLevel7: Int? = null,
    @Field("mystic_arcanum_level_8")
    var mysticArcanumLevel8: Int? = null,
    @Field("mystic_arcanum_level_9")
    var mysticArcanumLevel9: Int? = null,
    var rageCount: Int? = null,
    var rageDamageBonus: Int? = null,
    var sneakAttack: ClassSpecificSneakAttack? = null,
    var songOfRestDie: Int? = null,
    var sorceryPoints: Int? = null,
    var unarmoredMovement: Long? = null,
    var wildShapeFly: Boolean? = null,
    var wildShapeMaxCr: Int? = null,
    var wildShapeSwim: Boolean? = null,
)
