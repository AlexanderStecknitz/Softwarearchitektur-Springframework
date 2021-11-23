package com.acme.artikel.service

import entity.Artikel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service Klasse für die Entität Artikel
 * Dies ist eine Mock-Klasse
 * @author [Alexander Stecknitz]
 */
@Service
class ArtikelReadService {

    /**
     * Sucht nach allen Artikeln
     * @return Ein Flow mit Artikeln
     */
    private fun findAll(): Flow<Artikel> {
        logger.debug("find: all")
        return flowOf(
            Artikel(
                id = 1,
                name = "Buch",
                einkaufsPreis = 12,
                verkaufsPreis = 30,
                bestand = 20,
            ),
            Artikel(
                id = 2,
                name = "Schraubenzieher",
                einkaufsPreis = 1,
                verkaufsPreis = 3,
                bestand = 34,
            ),
            Artikel(
                id = 3,
                name = "Gummibärchen",
                einkaufsPreis = 1,
                verkaufsPreis = 4,
                bestand = 98,
            ),
        )
    }

    /**
     * Sucht einen Artikel mit einer bestimmten ID
     * @param id Die ID des gesuchten Artikels
     * @return Ein Artikel mit der übergegeben ID
     * oder null wenn es keinen Artikel mit der entsprechenden ID gibt.
     */
    fun findById(id: Int?): Artikel? {
        logger.debug("findById={}", id)
        if (id != 1) return null
        return Artikel(
            id = id,
            name = "Handschuh",
            einkaufsPreis = 12,
            verkaufsPreis = 24,
            bestand = 10,
        )
    }

    /**
     * Sucht einen passenden Artikel zu den Query-Parametern
     * @param suchkriterien Die Suchkriterien für den Artikel
     * @return Gibt ein Flow mit Artikeln zurück die zu den Suchkriterien passen
     */
    @Suppress("ReturnCount")
    fun find(suchkriterien: Map<String, String>): Flow<Artikel> {
        logger.debug("find: suchkriterien={}", suchkriterien)

        if (suchkriterien.isEmpty()) {
            return findAll()
        }

        for ((key, value) in suchkriterien) {
            when (key) {
                "name" -> return findByName(value)
                "einkaufspreis" -> return findByEinkaufspreis(value.toInt())
                "verkaufspreis" -> return findByVerkaufspreis(value.toInt())
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
    private fun findByName(name: String): Flow<Artikel> {
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
    private fun findByEinkaufspreis(einkaufspreis: Int): Flow<Artikel> {
        return flowOf(
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
    }

    /**
     * Sucht einen Artikel mit einem bestimmten Verkaufspreis
     * @param verkaufspreis Der Verkaufspreis des gesuchten Artikels
     * @return Ein Artikel mit dem übergegeben Verkaufspreis
     */
    private fun findByVerkaufspreis(verkaufspreis: Int): Flow<Artikel> {
        return flowOf(
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
    }

    /**
     * Sucht einen Artikel mit einem bestimmten Bestand
     * @param bestand Der Bestand des gesuchten Artikels
     * @return Ein Artikel mit dem übergegeben Bestand
     */
    private fun findByBestand(bestand: Int): Flow<Artikel> {
        return flowOf(
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
    }

    /**
     * Konstanten für die Service Klasse
     */
    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelReadService::class.java)
    }
}
