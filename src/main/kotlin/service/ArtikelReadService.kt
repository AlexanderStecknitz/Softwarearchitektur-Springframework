package com.acme.artikel.service

import com.acme.artikel.entity.Artikel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.data.mongodb.core.ReactiveFindOperation
import org.springframework.data.mongodb.core.flow
import org.springframework.data.mongodb.core.query
import org.springframework.stereotype.Service

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
        logger.debug("findById={}", id)
        if (id != 1) return FindByIdResult.NotFound
        return FindByIdResult.Success(
            Artikel(
                id = id,
                name = "Handschuh",
                einkaufsPreis = 12,
                verkaufsPreis = 24,
                bestand = 10,
            ),
        )
    }

    /**
     * Sucht einen passenden Artikel zu den Query-Parametern
     * @param suchkriterien Die Suchkriterien für den Artikel
     * @return Gibt ein Flow mit Artikeln zurück die zu den Suchkriterien passen
     */
    @Suppress("ReturnCount")
    suspend fun find(suchkriterien: Map<String, String>): Flow<Artikel> {
        logger.debug("find: suchkriterien={}", suchkriterien)

        if (suchkriterien.isEmpty()) {
            return findAll()
        }

        for ((key, value) in suchkriterien) {
            when (key) {
                "name" -> return findByName(value)
                "einkaufsPreis" -> return findByEinkaufspreis(value.toInt())
                "verkaufsPreis" -> return findByVerkaufspreis(value.toInt())
                "bestand" -> return findByBestand(value.toInt())
            }
        }

        return emptyFlow()
    }

    /**
     * Sucht einen Artikel mit einem bestimmten Namen
     * @param name Der Name des gesuchten Artikels
     * @return Ein Artikel mit dem übergegeben Namen
     * oder alle, wenn name nicht angegeben wird
     */
    private suspend fun findByName(name: String): Flow<Artikel> {
        if (name.trim().isNotEmpty()) {
            return flowOf(
                Artikel(
                    id = 1,
                    name = name,
                    einkaufsPreis = 12,
                    verkaufsPreis = 30,
                    bestand = 20,
                ),
                Artikel(
                    id = 2,
                    name = name,
                    einkaufsPreis = 1,
                    verkaufsPreis = 3,
                    bestand = 34,
                ),
                Artikel(
                    id = 3,
                    name = name,
                    einkaufsPreis = 1,
                    verkaufsPreis = 4,
                    bestand = 98,
                ),
            )
        }
        return findAll()
    }

    /**
     * Sucht einen Artikel mit einem bestimmten Einkaufspreis
     * @param einkaufspreis Der Einkaufspreis des gesuchten Artikels
     * @return Ein Artikel mit dem übergegeben Einkaufspreis
     */
    private suspend fun findByEinkaufspreis(einkaufspreis: Int) =
        flowOf(
            Artikel(
                id = 1,
                name = "Tablet",
                einkaufsPreis = einkaufspreis,
                verkaufsPreis = 30,
                bestand = 20,
            ),
            Artikel(
                id = 2,
                name = "Laptop",
                einkaufsPreis = einkaufspreis,
                verkaufsPreis = 3,
                bestand = 34,
            ),
            Artikel(
                id = 3,
                name = "PC",
                einkaufsPreis = einkaufspreis,
                verkaufsPreis = 4,
                bestand = 98,
            ),
        )

    /**
     * Sucht einen Artikel mit einem bestimmten Verkaufspreis
     * @param verkaufspreis Der Verkaufspreis des gesuchten Artikels
     * @return Ein Artikel mit dem übergegeben Verkaufspreis
     */
    private suspend fun findByVerkaufspreis(verkaufspreis: Int) =
        flowOf(
            Artikel(
                id = 1,
                name = "Handschuh",
                einkaufsPreis = 20,
                verkaufsPreis = verkaufspreis,
                bestand = 20,
            ),
            Artikel(
                id = 2,
                name = "Kleid",
                einkaufsPreis = 10,
                verkaufsPreis = verkaufspreis,
                bestand = 34,
            ),
            Artikel(
                id = 3, name = "Hemd",
                einkaufsPreis = 50,
                verkaufsPreis = verkaufspreis,
                bestand = 98,
            ),
        )

    /**
     * Sucht einen Artikel mit einem bestimmten Bestand
     * @param bestand Der Bestand des gesuchten Artikels
     * @return Ein Artikel mit dem übergegeben Bestand
     */
    private suspend fun findByBestand(bestand: Int) =
        flowOf(
            Artikel(
                id = 1,
                name = "Gitarre",
                einkaufsPreis = 20,
                verkaufsPreis = 23,
                bestand = bestand,
            ),
            Artikel(
                id = 2,
                name = "Schlagzeug",
                einkaufsPreis = 10,
                verkaufsPreis = 21,
                bestand = bestand,
            ),
            Artikel(
                id = 3,
                name = "Posaune",
                einkaufsPreis = 50,
                verkaufsPreis = 60,
                bestand = bestand,
            ),
        )

    /**
     * Konstanten für die Service Klasse
     */
    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelReadService::class.java)
        const val timeoutShort = 500L
    }
}
