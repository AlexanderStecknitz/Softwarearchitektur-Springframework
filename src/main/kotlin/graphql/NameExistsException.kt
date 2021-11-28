package com.acme.artikel.graphql

import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.BAD_REQUEST

/**
 * Exception, falls der Name für den neu anzulegenden Artikel bereits existiert.
 *
 * @property name Der bereits existierende Name
 */
class NameExistsException(val name: String) : Exception()

/**
 * Fehlerklasse für GraphQL, falls eine [NameExistsException] geworfen wurde. Die Abbildung erfolgt in
 * [ExceptionResolverAdapter].
 * @property name Der bereits existierende Name
 */
class NameExistsError(private val name: String) : GraphQLError {
    /**
     * Message innerhalb von _Errors_ beim Response für einen GraphQL-Request.
     */
    override fun getMessage() = "Der Name $name existiert bereits."

    /**
     * Keine Angabe von Zeilen- und Spaltennummer der GraphQL-Mutation, falls die Emailadresse bereits existiert.
     */
    override fun getLocations() = null

    /**
     * `ErrorType` auf `BAD_REQUEST` setzen.
     */
    override fun getErrorType() = BAD_REQUEST
}
