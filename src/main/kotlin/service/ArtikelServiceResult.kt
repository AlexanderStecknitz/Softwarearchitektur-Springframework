package com.acme.artikel.service

import am.ik.yavi.core.ConstraintViolation
import com.acme.artikel.entity.Artikel

/**
 * Resultat-Typ für [ArtikelReadService.findById].
 */
sealed interface FindByIdResult {
    /**
     * Resultat-Typ, wenn ein Artikel gefunden wurde
     * @property artikel Der gefundene Artikel
     */
    data class Found(val artikel: Artikel) : FindByIdResult

    /**
     * Resultat-Typ, wenn kein Artikel gefunden wurde
     */
    object NotFound : FindByIdResult
}

/**
 * Resultat-Typ für [ArtikelWriteService.create]
 */
sealed interface CreateResult {
    /**
     * Resultat-Typ, wenn ein neuer Artikel erfolgreich angelegt wurde
     * @property artikel Der neu angelegte Artikel
     */
    data class Created(val artikel: Artikel) : CreateResult

    /**
     * Resultat-Typ, wenn der Name bereits existiert
     * @property name Der bereits existierende Name
     */
    data class NameExists(val name: String) : CreateResult

    /**
     * Resultat-Typ, wenn ein Artikel wegen Constraint-Verletzungen nicht angelegt wurde
     * @property violations Die verletzten Constraints
     */
    data class ConstraintViolations(val violations: Collection<ConstraintViolation>) : CreateResult
}

/**
 * Resultat-Typ für [ArtikelWriteService.update]
 */
sealed interface UpdateResult {

    /**
     * Resultat-Typ, wenn ein Artikel erfolgreich aktualisiert wurde
     * @property artikel Der aktualisierte Artikel
     */
    data class Updated(val artikel: Artikel) : UpdateResult

    /**
     * Resultat-Typ, wenn der Name bereits existiert
     * @property name Der bereits existierende Name
     */
    data class NameExists(val name: String) : UpdateResult

    /**
     * Resultat-Typ, wenn der Artikel wegen Constraint-Verletzungen nicht aktualisiert wurde
     * @property violations Die verletzten Constraints
     */
    data class ConstraintViolations(val violations: Collection<ConstraintViolation>) : UpdateResult

    /**
     * Resultat-Typ, wenn der Artikel nicht gefunden werden konnte
     */
    object NotFound : UpdateResult
}
