package com.subgarden.backend.graphql

import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.slf4j.LoggerFactory


class DebugDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    companion object {
        private val log = LoggerFactory.getLogger(DebugDataFetcherExceptionHandler::class.java)
    }

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error = ExceptionWhileDataFetching(path, exception, sourceLocation)
        log.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult().error(error).build()
    }

}
