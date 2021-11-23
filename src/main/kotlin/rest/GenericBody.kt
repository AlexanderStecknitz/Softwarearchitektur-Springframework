@file:Suppress("unused")

package com.acme.artikel.rest

sealed interface GenericBody {
    data class Text(val text: String) : GenericBody
    data class Values(val values: Map<String, String>) : GenericBody
}
