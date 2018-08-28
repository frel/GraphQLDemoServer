package com.subgarden.backend.types.db

import com.subgarden.backend.graphql.Item
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
                val uri = "http://192.168.0.11:8080/images/$uuid"
                Item(uuid, getName(it), uri)
            }

        }
    }
}