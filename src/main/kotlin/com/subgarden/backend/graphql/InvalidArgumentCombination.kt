package com.subgarden.backend.graphql

import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

class InvalidArgumentCombination(val errorMessage: String) : GraphQLError {

    override fun getMessage() = errorMessage


    override fun getErrorType(): ErrorType = ErrorType.OperationNotSupported

    override fun getLocations(): MutableList<SourceLocation> {
        return ArrayList()
    }
}