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
package com.acme.artikel.config.dev

import com.acme.artikel.entity.Artikel
import com.mongodb.reactivestreams.client.MongoCollection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.createCollection
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.indexOps
import org.springframework.data.mongodb.core.insert
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty.int32
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty.string
import org.springframework.data.mongodb.core.schema.MongoJsonSchema

// Default-Implementierungen in einem Interface gibt es ab Java 8, d.h. ab 2013 !!!
// Eine abstrakte Klasse kann uebrigens auch Properties / Attribute / Felder sowie einen Konstruktor haben.
// In C# gibt es "Default Interface Methods", damit man mit Xamarin Android- und iOS-Apps entwickeln kann.
// https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/proposals/csharp-8.0/default-interface-methods

/**
 * Interface, um im Profil _dev_ die (Test-) DB neu zu laden.
 *
 */
interface DbPopulate {
    /**
     * Bean-Definition, um einen CommandLineRunner für das Profil "dev" bereitzustellen,
     * damit die (Test-) DB neu geladen wird.
     * Flyway unterstützt nur relationale DB-Systeme https://flywaydb.org.
     * Evtl. ist mongeez https://github.com/mongeez/mongeez eine Option
     * @param mongo Template für MongoDB
     * @return CommandLineRunner
     */
    @Bean
    fun dbPopulate(mongo: ReactiveMongoOperations) = CommandLineRunner {
        val logger: Logger = LoggerFactory.getLogger(DbPopulate::class.java)
        logger.warn("Neuladen der Collection '$collection'")

        runBlocking {
            mongo.dropCollection<Artikel>().awaitSingleOrNull()

            createCollectionAndSchema(mongo, logger)
            createIndexName(mongo, logger)

            TestDaten.artikel.onEach { artikel ->
                mongo.insert<Artikel>().oneAndAwait(artikel)
                logger.warn("{}", artikel)
            }.collect()
            logger.warn("Collection '$collection' wurde neu geladen")
        }
    }

    @Suppress("MagicNumber", "LongMethod")
    private suspend fun createCollectionAndSchema(
        mongo: ReactiveMongoOperations,
        logger: Logger,
    ): MongoCollection<Document> {
        val schema = MongoJsonSchema.builder()
            .required(name, einkaufsPreis, verkaufsPreis, bestand)
            .properties(
                string(name),
                int32(einkaufsPreis),
                int32(verkaufsPreis),
                int32(bestand),
            )
            .build()
        logger.info(
            "createCollectionAndSchema: JSON Schema fuer '$collection': {}",
            schema.toDocument().toJson(),
        )
        return mongo.createCollection<Artikel>(CollectionOptions.empty().schema(schema)).awaitSingle()
    }

    private suspend fun createIndexName(mongo: ReactiveMongoOperations, logger: Logger): String {
        logger.warn("createIndexNachname: Index fuer '$name'")
        val idx = Index(name, ASC).named(name)
        return mongo.indexOps<Artikel>().ensureIndex(idx).awaitSingle()
    }

    private companion object {
        val collection = Artikel::class.simpleName?.replaceFirstChar { it.lowercase() }

        val name = Artikel::name.name
        val einkaufsPreis = Artikel::einkaufsPreis.name
        val verkaufsPreis = Artikel::verkaufsPreis.name
        val bestand = Artikel::bestand.name
        const val erzeugt = "erzeugt"
        const val aktualisiert = "aktualisiert"
    }
}
