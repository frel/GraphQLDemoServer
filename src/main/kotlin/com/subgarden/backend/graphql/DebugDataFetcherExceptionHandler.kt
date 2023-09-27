package com.subgarden.backend.graphql

import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture


class DebugDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    companion object {
        private val log = LoggerFactory.getLogger(DebugDataFetcherExceptionHandler::class.java)
    }

    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error = ExceptionWhileDataFetching(path, exception, sourceLocation)
        log.warn(error.message, exception)
        return CompletableFuture.completedFuture(DataFetcherExceptionHandlerResult.newResult().error(error).build())
    }

}
