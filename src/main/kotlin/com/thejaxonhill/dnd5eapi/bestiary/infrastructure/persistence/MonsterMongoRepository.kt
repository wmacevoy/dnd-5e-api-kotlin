package com.thejaxonhill.dnd5eapi.bestiary.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.repository.IndexedMongoRepository

interface MonsterMongoRepository : IndexedMongoRepository<MonsterDocument, String>