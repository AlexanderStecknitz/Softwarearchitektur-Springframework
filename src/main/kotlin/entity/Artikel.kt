package com.acme.artikel.entity

import java.util.UUID

/** Beschreibt einen Artikel
 *  @property id Eindeutige ID eines Artikels
 *  @property name Name des Artikels
 *  @property einkaufsPreis EinkaufsPreis des Artikels
 *  @property verkaufsPreis VerkaufsPreis des Artikels
 *  @property bestand Derzeitiger Bestand des Artikels
 */
data class Artikel(
    /**
     * Eindeutiger Schlüssel
     */
    val id: UUID?,
    /**
     * Name oder auch Bezeichnung
     */
    val name: String,
    /**
     * Der Einkaufspreis
     */
    val einkaufsPreis: Int,
    /**
     * Der Verkaufspreis
     */
    val verkaufsPreis: Int,
    /**
     * Bestandsgröße
     */
    val bestand: Int,
) {

    /**
     * Vergleich mit einem anderen Objekt oder null.
     * @param other Das zu vergleichende Objekt oder null
     * @return True, falls das zu vergleichende (Artikel-) Objekt die gleiche ID hat.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Artikel
        return other.id == this.id
    }

    /**
     * Hashwert aufgrund der ID.
     * @return Der Hashwert.
     */
    override fun hashCode() = id?.hashCode() ?: this::class.hashCode()
}
