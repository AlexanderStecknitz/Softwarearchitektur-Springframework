package com.acme.artikel.graphql

import com.acme.artikel.service.ArtikelReadService
import com.acme.artikel.service.FindByIdResult
import entity.Artikel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
@Suppress("unused")
class ArtikelQueryController(val service: ArtikelReadService) {

    @QueryMapping
    fun artikel(@Argument id: Int): Artikel {
        logger.debug("findById: id={}", id)

        val result = runBlocking { service.findById(id) }
        if(result is FindByIdResult.NotFound) {
            throw NotFoundException(id)
        }
        result as FindByIdResult.Found
        return result.artikel
    }

    @QueryMapping
    fun artikels(@Argument input: Map<String, String>): Flux<Artikel> {
        logger.debug("find: input={}", input)
        @Suppress("BlockingMethodInNonBlockingContext")
        val artikel = runBlocking {
            service.find(input)
                .onEach { artikel -> logger.debug("find: {}", artikel)}
                .asFlux()
        }
        return artikel
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ArtikelQueryController::class.java)
    }

}
