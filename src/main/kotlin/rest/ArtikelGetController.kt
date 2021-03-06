@file:Suppress("unused")

package com.acme.artikel.rest

import com.acme.artikel.service.ArtikelReadService
import com.acme.artikel.service.FindByIdResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * RestController für die HTTP-Methode GET
 * @property service ArtikelReadService Klasse
 */
@RestController
@RequestMapping(ArtikelGetController.API_PATH)
@Tag(name = "Artikel API")
@Suppress("RegExpUnexpectedAnchor")
class ArtikelGetController(private val service: ArtikelReadService) {

    /**
     * Liefert ein bestimmtes Mock-Objekte mit einer ID
     * @param id Die ID zum suchen eines Artikels
     * @return Bei gefundem Artikel ein 200 Response mit dem Artikel oder 404 bei einem nicht gefundenen
     */
    @GetMapping(path = ["/{id:$ID_PATTERN}"], produces = [HAL_JSON_VALUE])
    @Operation(summary = "Suche mit einer Artikel-ID", tags = ["Suchen"])
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Artikel wurde gefunden",
        ),
        ApiResponse(
            responseCode = "404",
            description = "Artikel konnte nicht gefunden werden",
        ),
    )
    suspend fun findById(@PathVariable id: UUID?): ResponseEntity<ArtikelModel> {
        logger.debug("findById: id={}", id)
        val artikel = when (val result = service.findById(id)) {
            is FindByIdResult.Success -> result.artikel
            is FindByIdResult.NotFound -> return notFound().build()
        }
        val artikelModel = ArtikelModel(artikel)
        val baseUri = "https://..."
        val idUri = "$baseUri/${artikel.id}"
        val selfLink = Link.of(idUri)
        val listLink = Link.of(baseUri, LinkRelation.of("list"))
        artikelModel.add(selfLink, listLink)
        logger.trace("findById: {}", artikel)
        return ok(artikelModel)
    }

    /**
     * Sucht einen bestimmten Artikel mit den angegebenen Query-Parametern
     * @param queryParams Die Query-Parameter als Map
     * @return Bei gefundem Artikel ein 200 Response mit dem Artikel oder
     * 404 bei einem nicht gefundenen. Sollten  die Query-Parameter leer sein werden alle Artikel ausgegeben
     */
    @GetMapping(produces = [HAL_JSON_VALUE])
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
            responseCode = "404",
            description = "Keine Artikel gefunden",
        ),
    )
    suspend fun find(@RequestParam queryParams: Map<String, String>): ResponseEntity<CollectionModel<ArtikelModel>> {
        logger.debug("find: queryParams={}", queryParams)
        val modelList = mutableListOf<ArtikelModel>()
        val baseUri = "https://..."
        service.find(queryParams)
            .map { artikel ->
                val model = ArtikelModel(artikel)
                model.add(Link.of("$baseUri/${artikel.id}"))
            }
            .toList(modelList)
        if (modelList.isEmpty()) {
            return notFound().build()
        }
        return ok(CollectionModel.of(modelList))
    }

    /**
     * Konstante für den Rest-Controller
     */
    companion object {
        /**
         * Konstante für den API-Path
         */
        const val API_PATH = "/api"

        private const val HEX_PATTERN = "[\\dA-Fa-f]"

        /**
         * Muster für eine UUID. `$HEX_PATTERN{8}-($HEX_PATTERN{4}-){3}$HEX_PATTERN{12}` enthält eine _capturing group_
         * und ist nicht zulässig.
         */
        const val ID_PATTERN = "$HEX_PATTERN{8}-$HEX_PATTERN{4}-$HEX_PATTERN{4}-$HEX_PATTERN{4}-$HEX_PATTERN{12}"

        private val logger: Logger = LoggerFactory.getLogger(ArtikelGetController::class.java)
    }
}
