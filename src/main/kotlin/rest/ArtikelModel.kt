@file:Suppress("unused")

package com.acme.artikel.rest

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import entity.Artikel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtikelModel
        return name == other.name
    }

    override fun hashCode() = name.hashCode()

}
