package com.acme.artikel.service

import ch.qos.logback.core.joran.spi.ConsoleTarget.findByName
import com.acme.artikel.rest.ArtikelController
import entity.Artikel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service Klasse für die Entität Artikel
 * Dies ist eine Mock-Klasse
 */
@Service
class ArtikelService {

    val Logger = LoggerFactory

    /**
     * Gibt alle Mock-Objekte zurück
     */
    fun findAll(): Flow<Artikel> = flowOf(
        Artikel(id = 1, name = "Buch", einkaufsPreis = 12, verkaufsPreis = 30, bestand = 20),
        Artikel(id = 2, name = "Schraubenzieher", einkaufsPreis = 1, verkaufsPreis = 3, bestand = 34),
        Artikel(id = 3, name = "Gummibärchen", einkaufsPreis = 1, verkaufsPreis = 4, bestand = 98),
    )

    /**
     * Gibt ein Mock-Objekt zurück mit einer bestimmten ID
     */
    fun findById(id: Int?): Artikel? {
        id?.let { return Artikel(id = id, name = "Buch", einkaufsPreis = 12, verkaufsPreis = 30, bestand = 20)}
        return null
    }

    @Suppress("ReturnCount")
    suspend fun find(suchkriterien: Map<String, String>): Flow<Artikel> {
        logger.debug("find: suchkriterien={}", suchkriterien)

        if (suchkriterien.isEmpty()) {
            return findAll()
        }

        for ((key, value) in suchkriterien) {
            when (key) {
                "name" -> {
                    val kunde = findByName(value) ?: return emptyFlow()
                    return flow { emit(kunde) }
                }
                "nachname" -> return findByNachname(value)
            }
        }

        return emptyFlow()
    }

    suspend fun findByNachname(): Artikel {

    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelController::class.java)
    }
}
