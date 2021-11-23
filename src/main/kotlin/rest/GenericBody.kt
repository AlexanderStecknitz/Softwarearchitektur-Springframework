@file:Suppress("unused")

package com.acme.artikel.rest

/**
 * Allgemeiner ResponseBody für HTTP Schreibmethoden
 */
sealed interface GenericBody {
    /**
     * Body mit einem textuellen Wert
     * @property text Der Text
     */
    data class Text(val text: String) : GenericBody

    /**
     * Body, wenn es mehrere Werte, wie z.B. Constraint-Verletzungen, gibt
     * @property values Die verletzten Constraints
     */
    data class Values(val values: Map<String, String>) : GenericBody
}
