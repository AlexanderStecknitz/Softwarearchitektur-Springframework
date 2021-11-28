package com.acme.artikel.graphql

import am.ik.yavi.core.ConstraintViolation
import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.BAD_REQUEST

class ConstraintViolationsException(val violations: Collection<ConstraintViolation>): Exception()

class ConstraintViolationsError(private val violation: ConstraintViolation): GraphQLError {
    override fun getMessage() = violation.message()
    override fun getLocations() = null
    override fun getErrorType() = BAD_REQUEST
    override fun getPath() = listOf("input") + violation.name().split(".")
}
