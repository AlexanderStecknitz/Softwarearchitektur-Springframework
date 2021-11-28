package com.acme.artikel.graphql

import entity.Artikel

/** ValueObject für das Neuanlegen und Ändern eines neuen Artikels
 *  @property name Name des Artikels
 *  @property einkaufsPreis EinkaufsPreis des Artikels
 *  @property verkaufsPreis VerkaufsPreis des Artikels
 *  @property bestand Derzeitiger Bestand des Artikels
 */
data class ArtikelInput(
    val name: String,
    val einkaufsPreis: Int,
    val verkaufsPreis: Int,
    val bestand: Int,
) {
    /**
     * Konvertierung in ein Objekt des Anwendungskerns [Artikel]
     * @return Artikelobjekt für den Anwendungskern
     */
    fun toArtikel() = Artikel(
        id = null,
        name = name,
        einkaufsPreis = einkaufsPreis,
        verkaufsPreis = verkaufsPreis,
        bestand = bestand,
    )
}
