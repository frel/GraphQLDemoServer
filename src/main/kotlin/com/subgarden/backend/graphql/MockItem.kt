package com.subgarden.backend.graphql


interface MockItem {
    val uuid: String
    val title: String
    val ctype: Int
    val tags: List<String>
    val description: String?
    val subtitle: String?
    val creation_time: String?
    val owner_uuid: String
}

data class MockWallpaper(
        override val uuid: String,
        override val title: String,
        override val ctype: Int,
        override val tags: List<String>,
        override val description: String?,
        override val subtitle: String?,
        override val creation_time: String?,
        override val owner_uuid: String,
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
        override val creation_time: String?,
        override val owner_uuid: String,
        val streamUri: String?,
        val duration: Float?,
        val audioFlowerUrl: String?) : MockItem
