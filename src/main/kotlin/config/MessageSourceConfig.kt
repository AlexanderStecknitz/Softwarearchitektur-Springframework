/*
 * Copyright (C) 2021 - present Juergen Zimmermann, Hochschule Karlsruhe
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

import am.ik.yavi.factory.ValidatorFactory
import am.ik.yavi.message.MessageSourceMessageFormatter
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.util.*
import java.util.Locale.ENGLISH

/**
 * Die Bean-Definition für `MessageSource` wird bereitgestellt.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
 */
interface MessageSourceConfig {
    /**
     * Bean-Definition, um ein Objekt der Klasse `MessageSource` bereitzustellen, in dem die Properties
     * aus `src\main\resources\messages.properties` eingelesen sind.
     *
     * @return Ein Objekt zum Interface `MessageSource`
     */
    @Bean
    fun messageSource(): MessageSource = ReloadableResourceBundleMessageSource().apply {
        setBasename("classpath:messages")
        setDefaultEncoding("UTF-8")
    }

    /**
     * Bean-Definition, um ein Objekt der Klasse `ValidatorFactory` aus _Yavi_ zu erzeugen, das für Dependency
     * Injection verwendet werden kann. Siehe https://github.com/making/yavi#validator-factory.
     *
     * @return Ein Objekt der Klasse `ValidatorFactory`
     */
    @Bean
    fun validatorFactory(messageSource: MessageSource): ValidatorFactory {
        val messageFormatter = MessageSourceMessageFormatter(messageSource::getMessage)
        return ValidatorFactory(null, messageFormatter)
    }

    /**
     * Konstante für Messages
     */
    companion object {
        /**
         * Englisch als Defaultwert für Locale.
         */
        val DEFAULT_LOCALE: Locale = ENGLISH
    }
}
