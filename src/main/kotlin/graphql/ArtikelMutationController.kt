package com.acme.artikel.graphql

import com.acme.artikel.service.ArtikelWriteService
import com.acme.artikel.service.CreateResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
@Suppress("unused")
class ArtikelMutationController(val service: ArtikelWriteService) {

    @MutationMapping
    fun create(@Argument input: ArtikelInput): CreatePayLoad {
        logger.debug("create: inpute={}", input)
        val artikel = input.toArtikel()
        logger.debug("create: artikel={}", artikel)

        return when(val result = service.create(artikel)) {
            is CreateResult.Created -> CreatePayLoad(result.artikel.id)
            is CreateResult.ConstraintViolations -> throw ConstraintViolationsException(violations = result.violations)
            is CreateResult.NameExists -> throw NameExistsException(result.name)
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelMutationController::class.java)
    }
}
