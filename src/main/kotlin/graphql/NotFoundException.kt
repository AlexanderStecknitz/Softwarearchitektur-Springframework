package com.acme.artikel.graphql

import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.NOT_FOUND

class NotFoundException(val id: Int): Exception()
class NotFoundError(private val id: Int): GraphQLError {
    override fun getMessage() = "Kein Artikel mit der ID $id gefunden"
    override fun getLocations() = null
    override fun getErrorType() = NOT_FOUND
}
