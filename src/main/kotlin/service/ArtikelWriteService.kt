package com.acme.artikel.service

import com.acme.artikel.entity.Artikel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Random

/**
 * Service Klasse für die HTTP-Methoden PUT und POST
 * @constructor Einen ArtikelService mit einem injezierten ValidatorFactory erzeugen
 * @author [Alexander Stecknitz]
 */
@Service
class ArtikelWriteService(private val validator: ArtikelValidator, private val readService: ArtikelReadService) {

    /**
     * Einen neuen Artikel anlegen
     * @param artikel Das Objekt des neuanzulegenden Artikels
     * @return Ein Resultatobjekt mit entweder dem neu angelegten Artikel oder mit einem Fehlermeldungsobjekt
     */
    fun create(artikel: Artikel): CreateResult {
        logger.debug("create: {}", artikel)
        val violations = validator.validate(artikel = artikel)
        if (violations.isNotEmpty()) {
            logger.debug("create: violations={}", violations)
        }
        if (artikel.name[0].lowercaseChar() == 'a') {
            logger.debug("create: name {} existiert", artikel.name)
            return CreateResult.NameExists(artikel.name)
        }
        val neuerArtikel = artikel.copy(id = random.nextInt())
        logger.debug("create: {}", neuerArtikel)
        return CreateResult.Created(neuerArtikel)
    }

    /**
     * Einen vorhanden Artikel aktualisieren
     * @param artikel Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID des zu aktualisierenden Artikels
     * @return Ein Resultatobjekt mit entweder dem aktualisierenden Artikel oder mit einem Fehlermeldungsobjekt
     */
    fun update(artikel: Artikel, id: Int): UpdateResult {
        logger.debug("update: {}", artikel)
        val violations = validator.validate(artikel = artikel)
        if (violations.isNotEmpty()) {
            logger.debug("create: violations={}", violations)
        }
        if (readService.findById(id) is FindByIdResult.NotFound) {
            return UpdateResult.NotFound
        }

        if (artikel.name[0].lowercaseChar() == 'a') {
            logger.debug("update: name {} existiert", artikel.name)
            return UpdateResult.NameExists(artikel.name)
        }
        val neuerArtikel = artikel.copy(id = random.nextInt())
        logger.debug("update: artikel {}", artikel)
        return UpdateResult.Updated(neuerArtikel)
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelWriteService::class.java)
        val random: Random = Random()
    }
}
