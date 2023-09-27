package com.subgarden.backend.graphql

import graphql.ExecutionResult
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.schema.DataFetcher
import graphql.schema.GraphQLSchema
import graphql.schema.TypeResolver
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import kotlinx.coroutines.future.await


class GraphQLHandler(private val schema: GraphQLSchema) {

    constructor(schema: String, resolvers: Map<String, List<Pair<String, DataFetcher<*>>>>) :
            this(schema(schema, resolvers))

    companion object {

        private var typeResolver: TypeResolver = TypeResolver { env ->
            when (env.getObject<Any>()) {
                is MockWallpaper -> env.schema.getObjectType("Wallpaper")
                is MockAudio -> env.schema.getObjectType("Audio")
                else -> env.schema.getObjectType("Item")
            }
        }
        fun schema(schema: String, resolvers: Map<String, List<Pair<String, DataFetcher<*>>>>) : GraphQLSchema {
            val typeDefinitionRegistry = SchemaParser().parse(schema)

            val runtimeWiring = newRuntimeWiring()
                    .apply {
                        type(TypeRuntimeWiring.newTypeWiring("Item").typeResolver(typeResolver).build())
                        resolvers.forEach { (type, fields) ->
                            type(type) { builder ->
                                fields.forEach { (field, resolver) ->
                                    builder.dataFetcher(field, resolver)
                                }
                                builder }
                        }

                    }.build()

            return SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
        }
    }

    suspend fun execute(query: String, variables: Map<String, Any>): ExecutionResult {



        val graphql = GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(AsyncExecutionStrategy(DebugDataFetcherExceptionHandler())).build()

        val executionResult = graphql.executeAsync {
            builder -> builder
                .query(query)
                .variables(variables)
//                .operationName(op)
//                .context(context)
        }
        return executionResult.await()
    }
}