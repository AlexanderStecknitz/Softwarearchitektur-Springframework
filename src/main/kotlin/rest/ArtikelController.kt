@file:Suppress("unused")

package com.acme.artikel.rest

import com.acme.artikel.service.ArtikelService
import entity.Artikel
import graphql.normalized.FieldCollectorNormalizedQueryParams
import kotlinx.coroutines.flow.toList
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller zur Steuerung der eingegangenen Requests
 */
@RestController
@RequestMapping("/api")
class ArtikelController(private val service: ArtikelService) {

    /**
     * Liefert mehrere Mock-Objekte
     */
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    suspend fun findAll(): ResponseEntity<List<Artikel>> {
        val artikel = mutableListOf<Artikel>()
        service.findAll().toList(artikel)
        return ok(artikel)
    }

    /**
     * Liefert ein bestimmtes Mock-Objekte mit einer ID
     */
    @GetMapping(path = ["/{id:[1-9][0-9]*}"], produces = [APPLICATION_JSON_VALUE])
    fun findById(@PathVariable id: Int?): ResponseEntity<Artikel> {
        val artikel = service.findById(id) ?: return notFound().build()
        return ok(artikel)
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    suspend fun find(
        @RequestParam queryParams: Map<String,String>
    ): ResponseEntity<Collection<Artikel>> {
        val artikel = mutableListOf<Artikel>()
        service.find(queryParams).toList(artikel)
        if(artikel.isEmpty()) {
            return notFound().build()
        }
        return ok(null)
    }
}
