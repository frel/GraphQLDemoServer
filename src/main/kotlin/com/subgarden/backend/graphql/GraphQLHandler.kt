package com.subgarden.backend.graphql

import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import kotlinx.coroutines.experimental.future.await
import graphql.execution.AsyncExecutionStrategy



class GraphQLHandler(private val schema: GraphQLSchema) {

    constructor(schema: String, resolvers: Map<String, List<Pair<String, DataFetcher<*>>>>) :
            this(schema(schema, resolvers))

    companion object {
        fun schema(schema: String, resolvers: Map<String, List<Pair<String, DataFetcher<*>>>>) : GraphQLSchema {
            val typeDefinitionRegistry = SchemaParser().parse(schema)

            // TODO cleanup this mess.
            val runtimeWiring = newRuntimeWiring()
                    .apply {
                        resolvers.forEach {
                            (type, fields) -> this.type(type) { builder -> fields.forEach {
                            (field, resolver) -> builder.dataFetcher(field, resolver)
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