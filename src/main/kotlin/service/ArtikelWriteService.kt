package com.acme.artikel.service

import entity.Artikel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArtikelWriteService(private val validator: ArtikelValidator, private val readService: ArtikelReadService) {

    fun create(artikel: Artikel): CreateResult {
        logger.debug("create: {}", artikel)
        val violations = validator.validate(artikel = artikel)
        if(violations.isNotEmpty()) {
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

    fun update(artikel: Artikel, id: Int): UpdateResult {
        logger.debug("update: {}", artikel)
        val violations = validator.validate(artikel = artikel)
        if(violations.isNotEmpty()) {
            logger.debug("create: violations={}", violations)
        }
        if (readService.findById(id) == null) {
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

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelWriteService::class.java)
        val random: Random = Random()
    }

}
