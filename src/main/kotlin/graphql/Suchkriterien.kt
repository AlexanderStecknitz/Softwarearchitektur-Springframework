package com.acme.artikel.graphql

/**
 * Eine _Value_-Klasse für Eingabedaten passend zu `Suchkriterien` aus dem GraphQL-Schema.
 *
 * @constructor Ein Suchkriterien-Objekt mit den empfangenen Properties erzeugen.
 *
 * @property name Name
 * @property verkaufsPreis Verkaufspreis
 * @property einkaufsPreis Einkaufspreis
 * @property bestand Bestand
 */
data class Suchkriterien(
    val name: String?,
    val verkaufsPreis: String?,
    val einkaufsPreis: String?,
    val bestand: String?,
) {
    /**
     * Konvertierung in eine Map
     * @return Das konvertierte Map-Objekt
     */
    fun toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (name != null) {
            map["nachname"] = name
        }
        if (verkaufsPreis != null) {
            map["verkaufsPreis"] = verkaufsPreis
        }
        if (einkaufsPreis != null) {
            map["einkaufsPreis"] = einkaufsPreis
        }
        if (bestand != null) {
            map["bestand"] = bestand
        }
        return map
    }
}
