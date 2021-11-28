package com.acme.artikel.graphql

import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.BAD_REQUEST

class NameExistsException(val name: String): Exception()
class NameExistsError(private val name: String): GraphQLError {
    override fun getMessage() = "Der Name $name existiert bereits."
    override fun getLocations() = null
    override fun getErrorType() = BAD_REQUEST
}
