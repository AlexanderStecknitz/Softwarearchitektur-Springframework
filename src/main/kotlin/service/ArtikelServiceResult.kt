package com.acme.artikel.service

import entity.Artikel

/**
 * Resultat-Typ für [ArtikelWriteService.create]
 */
sealed interface CreateResult {
    data class Created(val artikel: Artikel) : CreateResult
    data class NameExists(val name: String) : CreateResult
    data class ConstraintViolations(val violations: Collection<ConstraintViolations>): CreateResult
}

/**
 * Resultat-Typ für [ArtikelWriteService.update]
 */
sealed interface UpdateResult {
    data class Updated(val artikel: Artikel) : UpdateResult
    data class NameExists(val name: String) : UpdateResult
    data class ConstraintViolations(val violations: Collection<ConstraintViolations>): UpdateResult
    object NotFound : UpdateResult
}
