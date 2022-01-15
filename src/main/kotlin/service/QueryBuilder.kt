package com.acme.artikel.service

import com.acme.artikel.entity.Artikel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.CriteriaDefinition
import org.springframework.data.mongodb.core.query.gte
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.regex
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap

/**
 * Singleton-Klasse, um _Criteria Queries_ für _MongoDB_ zu bauen.
 */
@Service
class QueryBuilder {
    /**
     * Eine `MultiValueMap` von _Spring_ wird in eine Liste von `CriteriaDefinition` für _MongoDB_ konvertiert,
     * um flexibel nach Artikel suchen zu können.
     * @param queryParams Die Query-Parameter in einer `MultiValueMap`.
     * @return [QueryBuilderResult] abhängig von den Query-Parametern.
     */
    fun build(queryParams: MultiValueMap<String, String>): QueryBuilderResult {
        val criteria = queryParams.map { (key, value) ->
            getCriteria(key, value)
        }

        if (criteria.isEmpty()) {
            return QueryBuilderResult.Failure
        }
        logger.debug("build: #criteria={}", criteria.size)

        val query = Query()
        criteria.forEach { crit ->
            if (crit == null) {
                return QueryBuilderResult.Failure
            }
            query.addCriteria(crit)
        }

        return QueryBuilderResult.Success(query)
    }

    private fun getCriteria(propertyName: String, propertyValues: List<String>?): CriteriaDefinition? {
        if (propertyValues?.size != 1) {
            return null
        }

        val value = propertyValues[0]
        return when (propertyName) {
            name -> getCriteriaName(value)
            einkaufsPreis -> getCriteriaEinkaufsPreis(value)
            verkaufsPreis -> getCriteriaVerkaufsPreis(value)
            bestand -> getCriteriaBestand(value)
            else -> null
        }
    }

    private fun getCriteriaName(name: String) =
        @Suppress("MagicNumber")
        if (name.length < 10) Artikel::name.regex(name, "i") else Artikel::name isEqualTo name

    private fun getCriteriaEinkaufsPreis(einkaufsPreis: String): CriteriaDefinition =
        Artikel::einkaufsPreis gte einkaufsPreis

    private fun getCriteriaVerkaufsPreis(verkaufsPreis: String): CriteriaDefinition? {
        val verkaufsPreisVal = verkaufsPreis.toBigDecimalOrNull() ?: return null
        return Artikel::verkaufsPreis gte verkaufsPreisVal
    }

    private fun getCriteriaBestand(bestand: String): CriteriaDefinition? {
        val bestandVal = bestand.toBigDecimalOrNull() ?: return null
        return Companion::bestand gte bestandVal
    }

    private companion object {
        private const val name = "name"
        private const val einkaufsPreis = "einkaufsPreis"
        private const val verkaufsPreis = "verkaufsPreis"
        private const val bestand = "bestand"
        private val logger: Logger = LoggerFactory.getLogger(QueryBuilder::class.java)
    }
}

/**
 * Resultat-Typ für [QueryBuilder.build]
 */
sealed interface  QueryBuilderResult {
    /**
     * Resultat-Typ, wenn die Query-Parameter korrekt sind.
     * @property query Das Query-Objekt
     */
    data class Success(val query: Query): QueryBuilderResult

    /**
     * Resultat-Typ, wenn mindestens 1 Query-Parameter falsch ist.
     */
    object Failure: QueryBuilderResult
}
