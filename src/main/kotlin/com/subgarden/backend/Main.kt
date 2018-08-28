package com.subgarden.backend

import com.subgarden.backend.graphql.GraphQLHandler
import com.subgarden.backend.graphql.GraphQLRequest
import com.subgarden.backend.types.db.MockData
import com.subgarden.backend.util.asResourceFile
import graphql.execution.DataFetcherResult
import graphql.language.SourceLocation
import graphql.schema.DataFetcher
import graphql.schema.StaticDataFetcher
import graphql.validation.ValidationError
import graphql.validation.ValidationErrorType
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
import kotlinx.coroutines.experimental.runBlocking
import java.io.File


const val GRAPHQL_SCHEMA = "schema.graphqls"
const val VERSION = "0.0.1"

fun main(args: Array<String>) {

    // Lazy init
    MockData.items

    runBlocking {

        val schema = GRAPHQL_SCHEMA.asResourceFile.readText()

        val fetchers = mapOf("Query" to listOf(
                versionFetcher(),
                itemFetcher(),
                allItemsFetcher()))

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

                post("/graphql") {
                    val request = call.receive<GraphQLRequest>()
                    val result = handler.execute(request.query, request.variables)

                    call.respond(mapOf("data" to result.getData<Any>(),
                            "errors" to result.errors))
                }
            }
        }
        server.start(wait = true)
    }
}

private fun versionFetcher() = "version" to StaticDataFetcher(VERSION)

private fun itemFetcher() = "item" to DataFetcher {
    val itemId = it.getArgument<String>("id")
    MockData.items.find { it.id == itemId }
}

private fun allItemsFetcher() = "allItems" to DataFetcher {
    if (it.containsArgument("first") && it.containsArgument("last")) {
        val errorMessage = "`first` and `last` must be used exclusively. Ignoring `last`."
        val error = ValidationError(ValidationErrorType.FieldsConflict, emptyList<SourceLocation>(), errorMessage)
        val data = MockData.items.take(it.getArgument<Int>("first"))
        DataFetcherResult(data, listOf(error))

    } else if (it.containsArgument("first")) {
        DataFetcherResult(MockData.items.take(it.getArgument<Int>("first")), emptyList())
    } else if (it.containsArgument("last")) {
        DataFetcherResult(MockData.items.takeLast(it.getArgument<Int>("last")), emptyList())
    } else {
        DataFetcherResult(MockData.items, emptyList())
    }
    // TODO For pagination add offset. See GitHub GraphQL API
}



