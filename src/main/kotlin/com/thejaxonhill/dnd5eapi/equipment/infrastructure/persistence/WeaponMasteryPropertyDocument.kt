package com.thejaxonhill.dnd5eapi.equipment.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "2024-weapon-mastery-properties")
class WeaponMasteryPropertyDocument(
    @Id
    var id: String? = null,
    var description: String? = null,
    var index: String? = null,
    var name: String? = null,
    var url: String? = null,
    var updatedAt: LocalDateTime? = null
)