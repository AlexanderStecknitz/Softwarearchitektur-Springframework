package com.acme.artikel.graphql

import java.util.*

/**
 * Entity-Klasse für das Resultat, wenn an der GraphQL-Schnittstelle ein neuer Artikel angelegt wurde.
 *
 * @property id ID des neu angelegten Kunden
 */
data class CreatePayLoad(val id: UUID?)
