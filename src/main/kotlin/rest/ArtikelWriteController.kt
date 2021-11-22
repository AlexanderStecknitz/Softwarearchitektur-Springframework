@file:Suppress("unused")

package com.acme.artikel.rest

import com.acme.artikel.rest.ArtikelGetController.Companion.API_PATH
import com.acme.artikel.service.ArtikelWriteService
import com.acme.artikel.service.CreateResult
import com.acme.artikel.service.UpdateResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.server.ServerHttpRequest
import org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(API_PATH)
@Tag(name = "Artikel API")
class ArtikelWriteController(private val service: ArtikelWriteService) {


    /**
     * Einen neuen Artikel-Datensatz anlegen.
     * @param artikelDTO Das Artikelobjekt als Data Transfer Objekt für den Request-Body
     */
    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Einen neuen Artikel anlegen")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Aktualisiert"),
        ApiResponse(responseCode = "400", description = "Ungültiger Name"),
    )
    fun create(
        @RequestBody artikelDTO: ArtikelDTO,
        request: ServerHttpRequest
    ) {
        logger.debug("create: {}", artikelDTO)

        when (val result = service.create(artikelDTO.toArtikel())) {
            is CreateResult.Created -> {
                logger.debug("create: {}", result)
            }
            is CreateResult.NameExists -> {
                logger.debug("create: name existiert bereits {}", result.name)
            }
            is CreateResult.ConstraintViolations -> {
                logger.debug("create: verletzung der violations")
            }
        }
    }

    /**
     * Einen vorhandenen Artikel-Datensatz überschreiben.
     * @param id ID des Artikels
     * @param artikelDTO Das Artikelobjekt als Data Transfer Objekt für den Request-Body
     */
    @PutMapping(path = ["/{id}"], consumes = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Einen Artikel mit neuen Werten aktualisieren", tags = ["Aktualisieren"])
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Aktualisiert"),
        ApiResponse(responseCode = "400", description = "Ungültiger Name"),
    )
    fun update(
        @PathVariable id: Int,
        @RequestBody artikelDTO: ArtikelDTO
    ) {
        logger.debug("update: id={}", id)
        logger.debug("update: {}", artikelDTO)

        when (val result = service.update(artikel = artikelDTO.toArtikel(), id)) {
            is UpdateResult.Updated -> {
                logger.debug("update: updated {}", result)
            }
            is UpdateResult.NameExists -> {
                logger.debug("update: name already exists{}", result.name)
            }
            is UpdateResult.NotFound -> {
                logger.debug("update: id not found{}", id)
            }
            is UpdateResult.ConstraintViolations -> {
                logger.debug("create: verletzung der violations")
            }
        }
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(ArtikelWriteController::class.java)
    }

}
