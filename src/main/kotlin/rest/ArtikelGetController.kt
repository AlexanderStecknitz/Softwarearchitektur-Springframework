@file:Suppress("unused")

package com.acme.artikel.rest

import com.acme.artikel.entity.Artikel
import com.acme.artikel.service.ArtikelReadService
import com.acme.artikel.service.FindByIdResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * RestController für die HTTP-Methode GET
 * @property service ArtikelReadService Klasse
 */
@RestController
@RequestMapping(ArtikelGetController.API_PATH)
@Tag(name = "Artikel API")
class ArtikelGetController(private val service: ArtikelReadService) {

    /**
     * Liefert ein bestimmtes Mock-Objekte mit einer ID
     * @param id Die ID zum suchen eines Artikels
     * @return Bei gefundem Artikel ein 200 Response mit dem Artikel oder 404 bei einem nicht gefundenen
     */
    @GetMapping(path = ["/{id:[1-9][0-9]*}"], produces = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Suche mit einer Artikel-ID", tags = ["Suchen"])
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Artikel wurde gefunden",
        ),
        ApiResponse(
            responseCode = "400",
            description = "Artikel konnte nicht gefunden werden",
        ),
    )
    fun findById(@PathVariable id: Int?): ResponseEntity<Artikel> {
        logger.debug("findById: id={}", id)
        val artikel = when (val result = service.findById(id)) {
            is FindByIdResult.Found -> result.artikel
            is FindByIdResult.NotFound -> return notFound().build()
        }
        logger.trace("findById: {}", artikel)
        return ok(artikel)
    }

    /**
     * Sucht einen bestimmten Artikel mit den angegebenen Query-Parametern
     * @param queryParams Die Query-Parameter als Map
     * @return Bei gefundem Artikel ein 200 Response mit dem Artikel oder
     * 404 bei einem nicht gefundenen. Sollten  die Query-Parameter leer sein werden alle Artikel ausgegeben
     */
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Suche mit Suchkriterien",
        tags = ["Suchen"]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "CollectionModel mit den Artikeln",
        ),
        ApiResponse(
            responseCode = "400",
            description = "Keine Artikel gefunden",
        ),
    )
    suspend fun find(@RequestParam queryParams: Map<String, String>): ResponseEntity<Collection<Artikel>> {
        logger.debug("find: queryParams={}", queryParams)
        val artikel = mutableListOf<Artikel>()
        service.find(queryParams).toList(artikel)
        if (artikel.isEmpty()) {
            return notFound().build()
        }
        logger.debug("find: {}", artikel)
        return ok(artikel)
    }

    companion object {
        /**
         * Konstante für den API-Path
         */
        const val API_PATH = "/api"
        private val logger: Logger = LoggerFactory.getLogger(ArtikelGetController::class.java)
    }
}
