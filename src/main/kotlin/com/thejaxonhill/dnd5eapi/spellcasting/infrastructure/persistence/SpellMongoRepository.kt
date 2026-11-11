package com.thejaxonhill.dnd5eapi.spellcasting.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.repository.IndexedMongoRepository

interface SpellMongoRepository : IndexedMongoRepository<SpellDocument, String>