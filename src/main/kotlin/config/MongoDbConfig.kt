/*
 * Copyright (C) 2016 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.artikel.config

import com.acme.artikel.entity.Artikel
import com.mongodb.WriteConcern.ACKNOWLEDGED
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.WriteConcernResolver
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import reactor.kotlin.core.publisher.toMono
import java.util.*

/**
 * Spring-Konfiguration für Enum-Konvertierungen beim Zugriff auf _MongoDB_.
 *
 * @author Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
 */
interface MongoDbConfig {
    /**
     * Bean zur Generierung der Kunde-ID beim Anlegen eines neuen Kunden
     * @return Kunde-Objekt mit einer Kunde-ID
     */
    @Bean
    fun generateKundeId() = ReactiveBeforeConvertCallback<Artikel> { artikel, _ ->
        if (artikel.id == null) {
            artikel.copy(id = UUID.randomUUID(), name = artikel.name.lowercase())
        } else {
            artikel
        }.toMono()

        // Mono<> von Project Reactor ist vergleichbar mit
        // Task<> bei async/await von C# https://docs.microsoft.com/dotnet/csharp/programming-guide/concepts/async
        // Promise bei async/await von JavaScript und TypeScript https://javascript.info/async-await
    }

    // /**
    //  * Bean für Transaktionen ab MongoDB 4 mit Installation als ReplicaSet.
    //  * @return Transaktionsmanager für Transaktionen
    //  */
    // @Bean
    // fun transactionManager(factory: ReactiveMongoDatabaseFactory) = ReactiveMongoTransactionManager(factory)

    /**
     * Bean für Optimistische Synchronisation
     * @return ACKNOWLEDGED als "WriteConcern" für MongoDB
     */
    @Suppress("unused")
    fun writeConcernResolver() = WriteConcernResolver { ACKNOWLEDGED }
}
