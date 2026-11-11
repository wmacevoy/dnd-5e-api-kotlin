package com.thejaxonhill.dnd5eapi.character.application.port

interface AbilityScoreImageProvider {
    fun getImage(index: String): ByteArray
}