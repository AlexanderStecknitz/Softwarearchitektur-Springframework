package com.acme.artikel.graphql

import graphql.GraphQLError
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class ExceptionResolverAdapter : DataFetcherExceptionResolverAdapter() {

    override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? =
        when(ex) {
            is NotFoundException -> NotFoundError(ex.id)
            is NameExistsException -> NameExistsError(ex.name)
            else -> super.resolveToSingleError(ex, env)
        }

    override fun resolveToMultipleErrors(ex: Throwable, env: DataFetchingEnvironment) =
        if (ex is ConstraintViolationsException) {
            ex.violations.map { violation -> ConstraintViolationsError(violation = violation) }
                .onEach { error -> logger.debug("error.message={}", error.message) }
        } else {
            super.resolveToMultipleErrors(ex, env)
        }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ExceptionResolverAdapter::class.java)
    }

}
