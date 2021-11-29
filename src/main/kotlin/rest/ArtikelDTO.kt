package com.acme.artikel.rest

import com.acme.artikel.entity.Artikel

/** ValueObject für das Neuanlegen und Ändern eines neuen Artikels
 *  @property name Name des Artikels
 *  @property einkaufsPreis EinkaufsPreis des Artikels
 *  @property verkaufsPreis VerkaufsPreis des Artikels
 *  @property bestand Derzeitiger Bestand des Artikels
 */
data class ArtikelDTO(
    val name: String,
    val einkaufsPreis: Int,
    val verkaufsPreis: Int,
    val bestand: Int,
) {
    /**
     * Konvertierung in ein Objekt des Anwendungskerns
     * @return Artikelobjekt für den Anwendungskern
     */
    fun toArtikel() = Artikel(
        id = null,
        name = name,
        einkaufsPreis = einkaufsPreis,
        verkaufsPreis = verkaufsPreis,
        bestand = bestand,
    )

    /**
     * Vergleich mit einem Objekt oder null
     * @param other Das zu vergleichende Objekt oder null
     * @return True, dalls das zu vergleichende Objekt den gleichen Namen hat.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtikelDTO
        return name == other.name
    }

    /**
     * Rückhabewert
     * @return hashCode von Name.
     */
    override fun hashCode() = name.hashCode()
}
