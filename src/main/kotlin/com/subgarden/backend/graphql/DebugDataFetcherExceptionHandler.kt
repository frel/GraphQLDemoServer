package com.subgarden.backend.graphql

import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import org.slf4j.LoggerFactory

class DebugDataFetcherExceptionHandler : DataFetcherExceptionHandler {


    companion object {
        private val log = LoggerFactory.getLogger(DebugDataFetcherExceptionHandler::class.java)
    }

    override fun accept(handlerParameters: DataFetcherExceptionHandlerParameters) {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.field.sourceLocation
        val path = handlerParameters.path

        val error = ExceptionWhileDataFetching(path, exception, sourceLocation)
        handlerParameters.executionContext.addError(error)
        log.warn(error.message, exception)
    }


}
