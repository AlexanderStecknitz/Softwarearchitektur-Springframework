package com.acme.artikel.service

import com.acme.artikel.entity.Artikel
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withTimeout
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.insert
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Service Klasse für die HTTP-Methoden PUT und POST
 * @constructor Einen ArtikelService mit einem injezierten ValidatorFactory erzeugen
 * @author [Alexander Stecknitz]
 */
@Service
class ArtikelWriteService(
    private val mongo: ReactiveFluentMongoOperations,
    @Lazy private val validator: ArtikelValidator,
    @Lazy private val readService: ArtikelReadService,
) {

    /**
     * Einen neuen Artikel anlegen
     * @param artikel Das Objekt des neuanzulegenden Artikels
     * @return Ein Resultatobjekt mit entweder dem neu angelegten Artikel oder mit einem Fehlermeldungsobjekt
     */
    suspend fun create(artikel: Artikel): CreateResult {
        logger.debug("create: {}", artikel)
        val violations = validator.validate(artikel = artikel)
        if (violations.isNotEmpty()) {
            logger.debug("create: violations={}", violations)
            return CreateResult.ConstraintViolations(violations)
        }

        val neuerArtikel = withTimeout(timeout) {
            mongo.insert<Artikel>().oneAndAwait(artikel.copy())
        }
        checkNotNull(neuerArtikel) { "Fehler beim Neuanlegen des Artikels" }
        logger.debug("create: {}", neuerArtikel)
        return CreateResult.Success(neuerArtikel)
    }

    /**
     * Einen vorhanden Artikel aktualisieren
     * @param artikel Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID des zu aktualisierenden Artikels
     * @return Ein Resultatobjekt mit entweder dem aktualisierenden Artikel oder mit einem Fehlermeldungsobjekt
     */
    suspend fun update(artikel: Artikel, id: UUID): UpdateResult {
        val violations = validator.validate(artikel)
        if (violations.isNotEmpty()) {
            return UpdateResult.ConstraintViolations(violations)
        }
        mongo as ReactiveMongoTemplate
        val artikelCache: MutableCollection<*> = mongo.converter.mappingContext.persistentEntities
        val artikelDb = readService.findById(id)
        if (artikelDb is FindByIdResult.NotFound) {
            return UpdateResult.NotFound
        }
        artikelCache.remove(artikelDb)
        val neuerArtikel = artikel.copy(id = id)
        logger.trace("update: neuerArtikel= {}", neuerArtikel)

        return withTimeout(timeout) {
            val artikelUpdated = mongo.save(neuerArtikel).awaitSingle()
            UpdateResult.Success(artikelUpdated)
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelWriteService::class.java)
        const val timeout = 500L
    }
}
