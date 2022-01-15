@file:Suppress("StringLiteralDuplication")

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
import kotlinx.coroutines.flow.flow

// Default-Implementierungen in einem Interface gibt es ab Java 8, d.h. ab 2013 !!!
// Eine abstrakte Klasse kann uebrigens auch Properties / Attribute / Felder sowie einen Konstruktor haben.
// In C# gibt es "Default Interface Methods", damit man mit Xamarin Android- und iOS-Apps entwickeln kann.
// https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/proposals/csharp-8.0/default-interface-methods

/**
 * Testdaten, um im Profil _dev_ die (Test-) DB neu zu laden.
 * In einem **richtigen Projekt**: eine JSON-Datei verwenden
 */
object TestDaten {
    /**
     * Testdaten für Artikel
     */
    @Suppress("MagicNumber", "UnderscoresInNumericLiterals")
    val artikel = flow {
        emit(
            Artikel(
                id = 1,
                name = "Gitarre",
                einkaufsPreis = 20,
                verkaufsPreis = 23,
                bestand = 2,
            )
        )
        emit(
            Artikel(
                id = 2,
                name = "Schlagzeug",
                einkaufsPreis = 20,
                verkaufsPreis = 23,
                bestand = 5,
            ),
        )
        emit(
            Artikel(
                id = 3,
                name = "Posaune",
                einkaufsPreis = 50,
                verkaufsPreis = 60,
                bestand = 10,
            )
        )
        emit(
            Artikel(
                id = 4,
                name = "Handschuh",
                einkaufsPreis = 20,
                verkaufsPreis = 40,
                bestand = 20,
            )
        )
        emit(
            Artikel(
                id = 5,
                name = "Kleid",
                einkaufsPreis = 10,
                verkaufsPreis = 14,
                bestand = 34,
            )
        )
        emit(
            Artikel(
                id = 6, name = "Hemd",
                einkaufsPreis = 50,
                verkaufsPreis = 85,
                bestand = 98,
            )
        )
        emit(
            Artikel(
                id = 7,
                name = "PC",
                einkaufsPreis = 2,
                verkaufsPreis = 4,
                bestand = 98,
            )
        )
    }
}
