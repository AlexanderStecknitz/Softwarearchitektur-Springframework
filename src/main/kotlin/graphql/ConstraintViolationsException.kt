package com.acme.artikel.graphql

import am.ik.yavi.core.ConstraintViolation
import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.BAD_REQUEST

/**
 * Exception, falls die Werte für den neu anzulegenden Artikel nicht valide sind.
 *
 * @property violations Die verletzten Constraints
 */
class ConstraintViolationsException(val violations: Collection<ConstraintViolation>) : Exception()

/**
 * Fehlerklasse für GraphQL, falls eine [ConstraintViolationsException] geworfen wurde. Die Abbildung erfolgt in
 * [ExceptionResolverAdapter].
 *
 * @property violation Das verletzte Constraint
 */
class ConstraintViolationsError(private val violation: ConstraintViolation) : GraphQLError {
    /**
     * Message innerhalb von _Errors_ beim Response für einen GraphQL-Request.
     * @return Fehlermeldung zum verletzten Constraint
     */
    override fun getMessage() = violation.message()

    /**
     * Keine Angabe von Zeilen- und Spaltennummer der GraphQL-Mutation, falls Constraints verletzt sind.
     * @return null
     */
    override fun getLocations() = null

    /**
     * `ErrorType` auf `BAD_REQUEST` setzen.
     * @return `BAD_REQUEST`
     */
    override fun getErrorType() = BAD_REQUEST

    /**
     * Pfadangabe von der Wurzel bis zum fehlerhaften Datenfeld
     * @return Liste der Datenfelder von der Wurzel bis zum Fehler
     */
    override fun getPath() = listOf("input") + violation.name().split(".")
}
