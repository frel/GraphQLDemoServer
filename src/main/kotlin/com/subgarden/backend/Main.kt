package com.subgarden.backend

import com.subgarden.backend.graphql.GraphQLHandler
import com.subgarden.backend.graphql.GraphQLRequest
import com.subgarden.backend.graphql.MockItem
import com.subgarden.backend.types.db.MockData
import com.subgarden.backend.util.asResourceFile
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.StaticDataFetcher
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.serialization.gson.gson
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import java.io.File


const val GRAPHQL_SCHEMA = "schema.graphqls"
const val VERSION = "0.0.2"
const val IP_ADDRESS = "10.42.13.81"
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
                gson()
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

                post("/graphql") {

                    val request = call.receive<GraphQLRequest>()
                    val query = request.query
                    val variables = request.variables
                    val result = handler.execute(query, variables)

                    call.respond(result.toSpecification())
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

private fun allItemsFetcher() = "items" to DataFetcher { env ->
    val first: Int? = env.getArgument<Int>("first")
    val items = if (first != null) MockData.items.take(first) else MockData.items
    DataFetcherResult
        .newResult<List<MockItem>>()
        .data(items)
        .errors(emptyList())
        .build()
}



