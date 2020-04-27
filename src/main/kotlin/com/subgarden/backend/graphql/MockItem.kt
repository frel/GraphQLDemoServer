package com.subgarden.backend.graphql


interface MockItem {
    val uuid: String
    val title: String
    val ctype: Int
    val tags: List<String>
    val description: String?
    val subtitle: String?
    val creationTime: String?
    val ownerUuid: String
}

data class MockWallpaper(
        override val uuid: String,
        override val title: String,
        override val ctype: Int,
        override val tags: List<String>,
        override val description: String?,
        override val subtitle: String?,
        override val creationTime: String?,
        override val ownerUuid: String,
        val imageUrl: String,
        val microThumb: String,
        val width: Int,
        val height: Int) : MockItem

data class MockAudio(
        override val uuid: String,
        override val title: String,
        override val ctype: Int,
        override val tags: List<String>,
        override val description: String?,
        override val subtitle: String?,
        override val creationTime: String?,
        override val ownerUuid: String,
        val streamUri: String?,
        val duration: Float?,
        val audioFlowerUrl: String?) : MockItem
