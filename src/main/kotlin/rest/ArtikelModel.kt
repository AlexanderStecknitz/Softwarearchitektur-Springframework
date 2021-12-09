package com.acme.artikel.rest

import com.acme.artikel.entity.Artikel
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

/**
 *  Unveränderliche Daten eines Artikels an der Rest-Schnittstelle
 *  @property name Name des Artikels
 *  @property einkaufsPreis EinkaufsPreis des Artikels
 *  @property verkaufsPreis VerkaufsPreis des Artikels
 *  @property bestand Derzeitiger Bestand des Artikels
 */
@JsonPropertyOrder(
    "name", "einkaufsPreis", "verkaufsPreis", "bestand",
)
@Relation(collectionRelation = "artikel", itemRelation = "artikel")
data class ArtikelModel(
    val name: String,
    val einkaufsPreis: Int,
    val verkaufsPreis: Int,
    val bestand: Int,
) : RepresentationModel<ArtikelModel>() {
    constructor(artikel: Artikel) : this(
        artikel.name,
        artikel.einkaufsPreis,
        artikel.verkaufsPreis,
        artikel.bestand,
    )

    /**
     * Vergleich mit einem anderen Objekt oder null.
     * @param other Das zu vergleichende Objekt oder null
     * @return True, falls das zu vergleichende (Artikel-) Objekt der gleiche name hat.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtikelModel
        return name == other.name
    }

    /**
     * Hashwert aufgrund des Namens.
     * @return Der Hashwert.
     */
    override fun hashCode() = name.hashCode()
}
