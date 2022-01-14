package com.acme.artikel.graphql

import com.acme.artikel.service.ArtikelWriteService
import com.acme.artikel.service.CreateResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

/**
 * Eine _Controller_-Klasse für das Schreiben mit der GraphQL-Schnittstelle und den Typen aus dem GraphQL-Schema.
 *
 * @constructor Einen ArtikelMutationController mit einem injizierten [ArtikelWriteService] erzeugen.
 *
 * @property service Injiziertes Objekt von [ArtikelWriteService]
 */
@Controller
@Suppress("unused")
class ArtikelMutationController(val service: ArtikelWriteService) {

    /**
     * Einen neuen Artikel anlegen
     * @param input Die Eingabedaten für einen neuen Artikel
     * @return Die generierte ID für den neuen Artikel
     * @throws ConstraintViolationsException, falls Constraints verletzt sind
     * @throws NameExistsException, falls es bereits einen Artikel mit diesem Namen existiert
     */
    @MutationMapping
    suspend fun create(@Argument input: ArtikelInput): CreatePayLoad {
        logger.debug("create: inpute={}", input)
        val artikel = input.toArtikel()
        logger.debug("create: artikel={}", artikel)

        return when (val result = service.create(artikel)) {
            is CreateResult.Success -> CreatePayLoad(result.artikel.id)
            is CreateResult.ConstraintViolations -> throw ConstraintViolationsException(violations = result.violations)
            is CreateResult.NameExists -> throw NameExistsException(result.name)
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelMutationController::class.java)
    }
}
