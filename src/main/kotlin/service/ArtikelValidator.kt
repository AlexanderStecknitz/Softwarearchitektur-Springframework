package com.acme.artikel.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.validator
import am.ik.yavi.core.ViolationMessage
import am.ik.yavi.message.MessageSourceMessageFormatter
import entity.Artikel
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

/**
 * Validierung von Objekten der Klasse [Artikel]
 *
 * @author [Alexander Stecknitz]
 */
@Service
class ArtikelValidator() {
    private val validator = validator<Artikel> {
        Artikel::name {
            notEmpty().message(
                ViolationMessage.of("artikel.nachname.notEmpty", "Es muss ein Name angegeben werden"),
            )
        }

        Artikel::verkaufsPreis {
            lessThanOrEqual(MIN_PREIS).message(
                ViolationMessage.of("artikel.verkaufspreis.min", "Der Verkaufspreis muss größer als -1 sein"),
            )
        }

        Artikel::einkaufsPreis {
            lessThanOrEqual(MIN_PREIS).message(
                ViolationMessage.of("artikel.einkaufspreis.min", "Der Einkaufspreis muss größer als -1 sein"),
            )
        }

        Artikel::bestand {
            lessThanOrEqual(MIN_BESTAND).message(
                ViolationMessage.of("artikel.bestand.min", "Der Bestand muss größer als -1 sein"),
            )
        }
    }

    /**
     * Validierung eines Entity-Objekts der Klasse [Artikel]
     * @param artikel Das zu validierende Artikel-Objekt
     * @return Eine Liste mit den Verletzungen der Constraints oder eine leere Liste
     */
    fun validate(artikel: Artikel) = validator.validate(artikel)

    /**
     * Konstante für die Validierung
     */
    private companion object {
        private const val MIN_PREIS = -1
        private const val MIN_BESTAND = -1
    }
}
