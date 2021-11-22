@file:Suppress("DEPRECATION")

package com.acme.artikel.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ViolationMessage
import am.ik.yavi.message.MessageSourceMessageFormatter
import entity.Artikel
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

@Service
class ArtikelValidator(messageSource: MessageSource) {
    private val validator = ValidatorBuilder.of<Artikel>()
        .messageFormatter(MessageSourceMessageFormatter(messageSource::getMessage))
        .konstraint(Artikel::name) {
            notEmpty().message(
                ViolationMessage.of(
                    "artikel.name.notEmpty",
                    "Es wird ein Name benötigt",
                ),
            )
        }
        .konstraint(Artikel::einkaufsPreis) {
            lessThanOrEqual(MIN_PREIS).message(
                ViolationMessage.of(
                    "artikel.einkaufsPreis.toLow",
                    "Einkaufspreis darf nicht 0 sein",
                ),
            )
        }
        .konstraint(Artikel::verkaufsPreis) {
            lessThanOrEqual(MIN_PREIS).message(
                ViolationMessage.of(
                    "artikel.verkaufsPreis.toLow",
                    "Verkaufspreis darf nicht 0 sein",
                ),
            )
        }
        .konstraint(Artikel::bestand) {
            lessThan(MIN_BESTAND).message(
                ViolationMessage.of(
                    "artikel.bestand.toLow",
                    "Der Bestand darf nicht unter 0 sein",
                ),
            )
        }
        .build()

    fun validate(artikel: Artikel) = validator.validate(artikel)

    companion object {
        const val MIN_PREIS = 0
        const val MIN_BESTAND = 0
    }
}
