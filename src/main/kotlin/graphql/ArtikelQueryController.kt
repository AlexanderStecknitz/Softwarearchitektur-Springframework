package com.acme.artikel.graphql

import com.acme.artikel.entity.Artikel
import com.acme.artikel.service.ArtikelReadService
import com.acme.artikel.service.FindByIdResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

/**
 * Eine _Controller_-Klasse für das Lesen mit der GraphQL-Schnittstelle und den Typen aus dem GraphQL-Schema.
 *
 * @constructor Einen ArtikelQueryController mit einem injizierten [ArtikelReadService] erzeugen.
 *
 * @property service Injiziertes Objekt von [ArtikelReadService]
 */
@Controller
@Suppress("unused")
class ArtikelQueryController(val service: ArtikelReadService) {

    /**
     * Suche anhand der Artikel-ID als Pfad-Parameter
     * @param id ID des zu suchenden Artikels
     * @return Der gefundene Artikel
     * @throws NotFoundException falls kein Artikel gefunden wurde
     */
    @QueryMapping
    fun artikel(@Argument id: Int): Artikel {
        logger.debug("findById: id={}", id)

        return when (val result = service.findById(id)) {
            is FindByIdResult.NotFound -> throw NotFoundException(id)
            is FindByIdResult.Found -> result.artikel
        }
    }

    /**
     * Suche mit diversen Suchkriterien
     * @param suchkriterien Suchkriterien und ihre Werte, z.B. `name` und `handschuh`
     * @return Der gefundene Artikel
     * @throws NotFoundException falls kein Artikel gefunden wurde
     */
    @QueryMapping
    fun artikels(@Argument("input") suchkriterien: Suchkriterien): Flow<Artikel> {
        logger.debug("find: input={}", suchkriterien)
        @Suppress("BlockingMethodInNonBlockingContext")
        return service.find(suchkriterien.toMap())
                .onEach { artikel -> logger.debug("find: {}", artikel) }
        }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelQueryController::class.java)
    }
}
