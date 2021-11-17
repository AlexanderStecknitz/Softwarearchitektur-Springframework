@file:Suppress("unused")

package com.acme.artikel.rest

import com.acme.artikel.service.ArtikelService
import entity.Artikel
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
 * Rest Controller zur Steuerung der eingegangenen Requests
 */
@RestController
@RequestMapping(ArtikelController.API_PATH)
@Tag(name="Artikel API")
class ArtikelController(private val service: ArtikelService) {

    /**
     * Liefert mehrere Mock-Objekte
     */
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Gibt alle Artikel zurück", tags = ["Suchen"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Alle Artikel gefunden"),
        ApiResponse(responseCode = "400", description = "Keinen Artikel gefunden")
    )
    suspend fun findAll(): ResponseEntity<List<Artikel>> {
        val artikel = mutableListOf<Artikel>()
        service.findAll().toList(artikel)
        return ok(artikel)
    }

    /**
     * Liefert ein bestimmtes Mock-Objekte mit einer ID
     */
    @GetMapping(path = ["/{id:[1-9][0-9]*}"], produces = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Suche mit einer Artikel-ID", tags = ["Suchen"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Artikel wurde gefunden"),
        ApiResponse(responseCode = "400", description = "Artikel konnte nicht gefunden werden")
    )
    fun findById(@PathVariable id: Int?): ResponseEntity<Artikel> {
        logger.debug("findById: id={}", id)
        val artikel = service.findById(id) ?: return notFound().build()
        logger.trace("findById: {}", artikel)
        return ok(artikel)
    }

    /**
     * Liefert ein bestimmtes Mock-Objekt mit den angegebenen Querys
     */
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Suche mit Suchkriterien", tags = ["Suchen"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "CollectionModel mit den Artikeln"),
        ApiResponse(responseCode = "400", description = "Keine Artikel gefunden")
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
        const val API_PATH = "/api"
        private val logger: Logger = LoggerFactory.getLogger(ArtikelController::class.java)
    }
}
