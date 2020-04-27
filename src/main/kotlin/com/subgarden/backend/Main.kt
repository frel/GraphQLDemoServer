package com.subgarden.backend

import com.subgarden.backend.graphql.GraphQLHandler
import com.subgarden.backend.graphql.GraphQLRequest
import com.subgarden.backend.graphql.MockItem
import com.subgarden.backend.types.db.MockData
import com.subgarden.backend.util.asResourceFile
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.StaticDataFetcher
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import java.io.File


const val GRAPHQL_SCHEMA = "schema.graphqls"
const val VERSION = "0.0.2"
const val IP_ADDRESS = "192.168.0.10"
const val PORT = "8080"

fun main() {

    // Lazy init
    MockData.items

    runBlocking {

        val schema = GRAPHQL_SCHEMA.asResourceFile.readText()

        val fetchers = mapOf(
            "Query" to listOf(
                versionFetcher(),
                itemFetcher(),
                allItemsFetcher()
            )
        )

        val handler = GraphQLHandler(schema, fetchers)

        val server = embeddedServer(Netty, port = 8080) {

            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }

            routing {

                get("/") {
                    call.respondText("Well, hello there ;)", ContentType.Text.Plain)
                }

                get("/images/{name}") {
                    val fileName = call.request.path().removePrefix("/images/")
                    val filePath = MockData.filePathMap[fileName]

                    if (filePath == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        val file = File(filePath)
                        call.respondFile(file)
                    }
                }

                @Suppress("USELESS_ELVIS")
                post("/graphql") {
                    val request = call.receive<GraphQLRequest>()
                    val query = request.query ?: ""
                    val variables = request.variables ?: emptyMap()
                    val result = handler.execute(query, variables)

                    call.respond(
                        mapOf(
                            "data" to result.getData<Any>(),
                            "errors" to result.errors
                        )
                    )
                }
            }
        }
        server.start(wait = true)
    }
}

private fun versionFetcher() = "version" to StaticDataFetcher(VERSION)

private fun itemFetcher() = "item" to DataFetcher { env ->
    val itemId = env.getArgument<String>("id")
    MockData.items.find { it.uuid == itemId }
}

private fun allItemsFetcher() = "items" to DataFetcher {
    DataFetcherResult
        .newResult<List<MockItem>>()
        .data(MockData.items)
        .errors(emptyList())
        .build()
}



