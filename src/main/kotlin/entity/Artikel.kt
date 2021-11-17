package entity

/** Entität Artikel
 *
 */
data class Artikel(
    /**
     * Eindeutiger Schlüssel
     */
    val id: Int?,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Artikel
        return other.id == this.id
    }

    override fun hashCode() = id?.hashCode() ?: this::class.hashCode()
}
