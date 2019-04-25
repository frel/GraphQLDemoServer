package com.subgarden.backend.types.db

import com.subgarden.backend.IP_ADDRESS
import com.subgarden.backend.PORT
import com.subgarden.backend.graphql.MockAudio
import com.subgarden.backend.graphql.MockItem
import com.subgarden.backend.graphql.MockWallpaper
import com.subgarden.backend.util.asResourceFile
import java.io.File
import java.util.*

class MockData {

    companion object {

        var filePathMap = mutableMapOf<String, String>()

        val items by lazy {

            fun getName(file: File): String {
                val pattern = "(?<name>.+)-\\d+_\\d+.".toRegex()
                return pattern.find(file.name)!!.groups["name"]?.value?.replace("-", " ") ?: "Untitled"
            }
            "images".asResourceFile.listFiles().map {

                val uuid = UUID.randomUUID().toString()
                filePathMap[uuid] = it.absolutePath
                val uri = "http://$IP_ADDRESS:$PORT/images/$uuid"
                MockWallpaper(
                        uuid,
                        getName(it),
                        0,
                        listOf("tag1", "tag2", "tag3"),
                        null,
                        "subtitle",
                        null,
                        uuid,
                        uri,
                        "base64encodedJpeg",
                        1,
                        2)

            }.toMutableList<MockItem>().apply {
                val uuid = UUID.randomUUID().toString()
                val uri = "http://$IP_ADDRESS:$PORT/audio/$uuid"
                    add(MockAudio(
                            uuid,
                            "some audio",
                            0,
                            listOf("tag1", "tag2", "tag3"),
                            null,
                            "subtitle",
                            null,
                            uuid,
                            uri,
                            30f,
                            null))
            }


        }
    }
}