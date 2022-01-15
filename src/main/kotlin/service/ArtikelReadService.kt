package com.acme.artikel.service

import com.acme.artikel.entity.Artikel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.data.mongodb.core.ReactiveFindOperation
import org.springframework.data.mongodb.core.awaitOneOrNull
import org.springframework.data.mongodb.core.flow
import org.springframework.data.mongodb.core.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap

/**
 * Service Klasse für die Entität Artikel
 * Dies ist eine Mock-Klasse
 * @author [Alexander Stecknitz]
 */
@Service
class ArtikelReadService(
    private val mongo: ReactiveFindOperation,
    @Lazy private val queryBuilder: QueryBuilder,
) {

    /**
     * Sucht nach allen Artikeln
     * @return Alle Artikel
     */
    private suspend fun findAll(): Flow<Artikel> = withTimeout(timeoutShort) {
        logger.debug("find: all")
        mongo.query<Artikel>()
            .flow()
            .onEach { logger.debug("findAll: {}", it) }
    }

    /**
     * Sucht einen Artikel mit einer bestimmten ID
     * @param id Die ID des gesuchten Artikels
     * @return Ein Artikel mit der übergegeben ID
     * oder null wenn es keinen Artikel mit der entsprechenden ID gibt.
     */
    suspend fun findById(id: Int?): FindByIdResult {
        val artikel = withTimeout(timeoutShort) {
            mongo.query<Artikel>()
                .matching(Artikel::id isEqualTo id)
                .awaitOneOrNull()
        } ?: return FindByIdResult.NotFound
        logger.debug("findById={}", artikel)
        return FindByIdResult.Success(artikel)
    }

    /**
     * Sucht einen passenden Artikel zu den Query-Parametern
     * @param suchkriterien Die Suchkriterien für den Artikel
     * @return Gibt ein Flow mit Artikeln zurück die zu den Suchkriterien passen
     */
    @Suppress("ReturnCount")
    suspend fun find(suchkriterien: MultiValueMap<String, String>): Flow<Artikel> {
        logger.debug("find: suchkriterien={}", suchkriterien)

        if (suchkriterien.isEmpty()) {
            return findAll()
        }

       val query = when(val builderResult = queryBuilder.build(suchkriterien)) {
           is QueryBuilderResult.Failure -> return emptyFlow()
           is QueryBuilderResult.Success -> builderResult.query
       }

        return withTimeout(timeoutLong) {
            mongo.query<Artikel>()
                .matching(query)
                .flow()
                .onEach { artikel -> logger.debug("find: {}", artikel) }
        }
    }
    /**
     * Konstanten für die Service Klasse
     */
    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelReadService::class.java)
        const val timeoutShort = 500L
        const val timeoutLong = 2000L
    }
}
