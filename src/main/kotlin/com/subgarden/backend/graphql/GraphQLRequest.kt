package com.subgarden.backend.graphql


data class GraphQLRequest(
    val query: String = "",
    val variables: Map<String, Any> = emptyMap()
)

