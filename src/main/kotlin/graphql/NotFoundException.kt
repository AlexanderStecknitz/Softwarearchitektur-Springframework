package com.acme.artikel.graphql

import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.NOT_FOUND

/**
 * Exception, falls mit dem Anwendungskern kein Artikel gefunden wird.
 *
 * @property id ID des nicht-vorhandenen Artikels
 */
class NotFoundException(val id: Int) : Exception()

/**
 * Fehlerklasse für GraphQL, falls eine [NotFoundException] geworfen wurde. Die Abbildung erfolgt in
 * [ExceptionResolverAdapter].
 *
 * @property id ID des nicht-vorhandenen Artikels
 */
class NotFoundError(private val id: Int) : GraphQLError {
    /**
     * Message innerhalb von _Errors_ beim Response für einen GraphQL-Request.
     */
    override fun getMessage() = "Kein Artikel mit der ID $id gefunden"

    /**
     * Keine Angabe von Zeilen- und Spaltennummer der GraphQL-Query, falls kein Artikel gefunden wurde.
     */
    override fun getLocations() = null

    /**
     * `ErrorType` auf `NOT_FOUND` setzen.
     */
    override fun getErrorType() = NOT_FOUND
}
