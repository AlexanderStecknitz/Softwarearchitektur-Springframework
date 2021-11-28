package com.acme.artikel.graphql

import com.acme.artikel.service.ArtikelReadService
import com.acme.artikel.service.FindByIdResult
import entity.Artikel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

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

        val result = runBlocking { service.findById(id) }
        if (result is FindByIdResult.NotFound) {
            throw NotFoundException(id)
        }
        result as FindByIdResult.Found
        return result.artikel
    }

    /**
     * Suche mit diversen Suchkriterien
     * @param input Suchkriterien und ihre Werte, z.B. `name` und `handschuh`
     * @return Der gefundene Artikel
     * @throws NotFoundException falls kein Artikel gefunden wurde
     */
    @QueryMapping
    fun artikels(@Argument input: Map<String, String>): Flux<Artikel> {
        logger.debug("find: input={}", input)
        @Suppress("BlockingMethodInNonBlockingContext")
        val artikel = runBlocking {
            service.find(input)
                .onEach { artikel -> logger.debug("find: {}", artikel) }
                .asFlux()
        }
        return artikel
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelQueryController::class.java)
    }
}
