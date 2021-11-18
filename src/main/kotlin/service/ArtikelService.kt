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
 */
@Service
class ArtikelService {

    /**
     * Gibt alle Mock-Objekte zurück
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
     * Gibt ein Mock-Objekt zurück mit einer bestimmten ID
     */
    fun findById(id: Int?): Artikel? {
        logger.debug("findById={}", id)
        val artikel = Artikel(
            id = 10,
            name = "Buch",
            einkaufsPreis = 12,
            verkaufsPreis = 30,
            bestand = 20,
        )
        if (artikel.id == id) {
            return artikel
        }
        return null
    }

    /**
     * Gibt ein passendes Mock-Objekt bei den Querys zurück
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
     * Gibt ein Mock-Objekt zurück mit einem bestimmten Namen
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
     * Gibt ein Mock-Objekt zurück mit einem bestimmten Einkaufspreis
     * Man geht hier davon aus, dass der Einkaufspreis nicht 0 sein kann
     */
    private fun findByEinkaufspreis(preis: Int): Flow<Artikel> {
        return flowOf(
            Artikel(
                id = 1,
                name = "Tablet",
                einkaufsPreis = preis,
                verkaufsPreis = 30,
                bestand = 20,
            ),
            Artikel(
                id = 2,
                name = "Laptop",
                einkaufsPreis = preis,
                verkaufsPreis = 3,
                bestand = 34,
            ),
            Artikel(
                id = 3,
                name = "PC",
                einkaufsPreis = preis,
                verkaufsPreis = 4,
                bestand = 98,
            ),
        )
    }

    /**
     * Gibt ein Mock-Objekt zurück mit einem bestimmten Verkaufspreis
     * Man geht hier davon aus, dass der Verkaufspreis nicht 0 sein kann
     */
    private fun findByVerkaufspreis(preis: Int): Flow<Artikel> {
        return flowOf(
            Artikel(
                id = 1,
                name = "Handschuh",
                einkaufsPreis = 20,
                verkaufsPreis = preis,
                bestand = 20,
            ),
            Artikel(
                id = 2,
                name = "Kleid",
                einkaufsPreis = 10,
                verkaufsPreis = preis,
                bestand = 34,
            ),
            Artikel(
                id = 3, name = "Hemd",
                einkaufsPreis = 50,
                verkaufsPreis = preis,
                bestand = 98,
            ),
        )
    }

    /**
     * Gibt ein Mock-Objekt zurück mit einem bestimmten Bestand
     */
    private fun findByBestand(bestand: Int): Flow<Artikel> {
        return flowOf(
            Artikel(
                id = 1,
                name = "Tablet",
                einkaufsPreis = 20,
                verkaufsPreis = bestand,
                bestand = 20,
            ),
            Artikel(
                id = 2,
                name = "Laptop",
                einkaufsPreis = 10,
                verkaufsPreis = bestand,
                bestand = 34,
            ),
            Artikel(
                id = 3,
                name = "PC",
                einkaufsPreis = 50,
                verkaufsPreis = bestand,
                bestand = 98,
            ),
        )
    }

    /**
     * Konstanten für die Service Klasse
     */
    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelService::class.java)
    }
}
