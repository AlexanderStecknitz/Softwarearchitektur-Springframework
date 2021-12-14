@file:Suppress("UnstableApiUsage")

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

//  Aufrufe
//  1) Microservice uebersetzen und starten
//        .\gradlew bootRun [-Dport=8081] [-Dhey=true] [--args='--debug'] [--continuous]
//        .\gradlew compileKotlin
//        .\gradlew compileTestKotlin
//
//  2) Microservice als selbstausfuehrendes JAR erstellen und ausfuehren
//        .\gradlew bootJar
//        java -jar build/libs/....jar --spring.profiles.active=dev
//        .\gradlew bootBuildImage [-Dtag='2.0.0']
//        .\gradlew jibDockerBuild  -DjavaVersion=11 -Dtag='1.0.0-jib' [-Ddebug=true]
//              erfordert die lokale Windows-Gruppe docker-users
//
//  3) Tests und QS
//        .\gradlew test [jacocoTestReport] [--rerun-tasks] [-Dfork=2] [--fail-fast]
//              EINMALIG>>   .\gradlew downloadAllure
//        .\gradlew allureServe
//        .\gradlew ktlint detekt
//
//  4) Sicherheitsueberpruefung durch OWASP Dependency Check und Snyk
//        .\gradlew dependencyCheckAnalyze --info
//        .\gradlew snyk-test
//
//  5) "Dependencies Updates"
//        .\gradlew versions
//        .\gradlew dependencyUpdates
//
//  6) API-Dokumentation erstellen
//        .\gradlew dokkaHtml
//
//  7) Entwicklerhandbuch in "Software Engineering" erstellen
//        .\gradlew asciidoctor asciidoctorPdf
//
//  8) "Dependency Verification"
//        ./gradlew --write-verification-metadata pgp,sha256 --export-keys
//
//  9) Projektreport erstellen
//        .\gradlew projectReport
//        .\gradlew dependencyInsight --dependency spring-security-rsa
//        .\gradlew dependencies
//        .\gradlew dependencies --configuration runtimeClasspath
//        .\gradlew buildEnvironment
//        .\gradlew htmlDependencyReport
//
//  10) Report ueber die Lizenzen der eingesetzten Fremdsoftware
//        .\gradlew generateLicenseReport
//
//  11) Daemon stoppen
//        .\gradlew --stop
//
//  12) Verfuegbare Tasks auflisten
//        .\gradlew tasks
//
//  13) Initialisierung des Gradle Wrappers in der richtigen Version
//      dazu ist ggf. eine Internetverbindung erforderlich
//        gradle wrapper --gradle-version=7.3.1 --distribution-type=bin

// https://github.com/gradle/kotlin-dsl/tree/master/samples
// https://docs.gradle.org/current/userguide/kotlin_dsl.html
// https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin

plugins {
    java
    idea
    jacoco
    `project-report`

    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.kotlinAllopen)
    alias(libs.plugins.kotlinNoarg)

    alias(libs.plugins.springBoot)
    //alias(libs.plugins.springAot)

    // https://github.com/radarsh/gradle-test-logger-plugin
    alias(libs.plugins.testLogger)

    // https://github.com/allure-framework/allure-gradle
    // https://docs.qameta.io/allure/#_gradle_2
    alias(libs.plugins.allure)

    // https://github.com/boxheed/gradle-sweeney-plugin
    alias(libs.plugins.sweeney)

    // https://github.com/arturbosch/detekt
    alias(libs.plugins.detekt)

    // https://github.com/Kotlin/dokka
    alias(libs.plugins.dokka)

    // https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin
    alias(libs.plugins.jib)

    // https://github.com/jeremylong/dependency-check-gradle
    alias(libs.plugins.owaspDependencycheck)

    // https://github.com/snyk/gradle-plugin
    alias(libs.plugins.snyk)

    // https://github.com/asciidoctor/asciidoctor-gradle-plugin
    // FIXME https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/597
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.asciidoctorPdf)
    // Leanpub als Alternative zu PDF: https://github.com/asciidoctor/asciidoctor-leanpub-converter

    // https://github.com/nwillc/vplugin
    alias(libs.plugins.nwillc)

    // https://github.com/ben-manes/gradle-versions-plugin
    alias(libs.plugins.benManes)

    // https://github.com/jk1/Gradle-License-Report
    alias(libs.plugins.licenseReport)

    // https://github.com/gradle-dependency-analyze/gradle-dependency-analyze
    // https://github.com/jaredsburrows/gradle-license-plugin
    // https://github.com/hierynomus/license-gradle-plugin
}

defaultTasks = mutableListOf("compileTestKotlin")
group = "com.acme"
version = "1.0.0"

repositories {
    mavenCentral()

    // https://github.com/spring-projects/spring-framework/wiki/Spring-repository-FAQ
    // https://github.com/spring-projects/spring-framework/wiki/Release-Process
    maven("https://repo.spring.io/release") { mavenContent { releasesOnly() } }
    maven("https://repo.spring.io/milestone") { mavenContent { releasesOnly() } }

    // Snapshots von Spring
    //maven("https://repo.spring.io/snapshot") { mavenContent { snapshotsOnly() } }

    // Snapshots von JaCoCo
    //maven("https://oss.sonatype.org/content/repositories/snapshots") {
    //    mavenContent { snapshotsOnly() }
    //    content { onlyForConfigurations("jacocoAgent", "jacocoAnt") }
    //}
}

/** Konfiguration fuer ktlint */
val ktlintCfg: Configuration by configurations.creating

// https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation
dependencies {
    // https://docs.gradle.org/current/userguide/managing_transitive_dependencies.html#sec:bom_import
    // https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-bom/pom.xml
    implementation(platform(libs.kotlinBom))
    implementation(platform(libs.coroutinesBom))
    // https://snyk.io/vuln/SNYK-JAVA-IONETTY-1042268
    // https://github.com/netty/netty/issues/8537
    implementation(platform(libs.nettyBom))
    //implementation(platform(libs.reactorBom))
    //implementation(platform(libs.jacksonBom))
    //implementation(platform(libs.springBom))
    //implementation(platform(libs.springDataBom))
    //implementation(platform(libs.springSecurityBom))
    //implementation(platform(libs.junitBom))
    implementation(platform(libs.allureBom))
    implementation(platform(libs.springBootBom))

    // kotlinx.reflect.lite unterstuetzt nur Namen von Parametern und deren Nullability
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // "Starters" enthalten sinnvolle Abhaengigkeiten, die man i.a. benoetigt
    // spring-boot-starter beinhaltet Spring Boot mit Actuator sowie spring-boot-starter-logging mit Logback
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-tomcat")
    implementation("org.springframework.boot:spring-boot-starter-json")
    // "Spring Boot Starter ist fuer Spring WebMvc" https://github.com/spring-projects/spring-boot/issues/26897
    implementation("org.springframework.hateoas:spring-hateoas")
    implementation(libs.springGraphQL)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-actuator") {
        // wegen Spring Native
        exclude(group = "io.micrometer", module = "micrometer-core")
    }

    //implementation(libs.javaNativeImage)

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.core.spring.reactor.debug-agent.enabled
    //implementation("io.projectreactor:reactor-tools")
    // Alternative: Valiktor https://github.com/valiktor/valiktor
    implementation(libs.yavi)
    // https://springdoc.org
    // https://github.com/springdoc/springdoc-openapi
    // https://www.baeldung.com/spring-rest-openapi-documentation
    // https://localhost:8080/swagger-ui.html
    implementation(libs.openapi)

    // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-devtools
    // https://www.vojtechruzicka.com/spring-boot-devtools
    // NICHT UNTERSTUETZT durch Spring Native
    // runtimeOnly(libs.devtools)

    runtimeOnly(libs.jansi)

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation(libs.bundles.kotestBundle)
    testImplementation(libs.mockk)
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.assertj", module = "assertj-core")
        exclude(group = "org.hamcrest", module = "hamcrest")
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
        exclude(group = "org.skyscreamer", module = "jsonassert")
        exclude(group = "org.xmlunit", module = "xmlunit-core")
    }
    testImplementation(libs.junitPlatformSuiteApi)
    testRuntimeOnly(libs.junitPlatformSuiteEngine)
    testImplementation("org.springframework.security:spring-security-test")

    ktlintCfg(libs.bundles.ktlint)

    dokkaHtmlPlugin(libs.dokkaHtml)

    // https://youtrack.jetbrains.net/issue/KT-27463
    @Suppress("UnstableApiUsage")
    constraints {
        implementation(libs.annotations)
        //implementation(libs.springHateoas)
        //implementation(libs.bundles.mongodb)
        implementation(libs.bundles.tomcat)
        //implementation(libs.graphqlJava)
        //implementation(libs.bundles.graphqlJavaBundle)
        //implementation(libs.graphqlJava)
        implementation(libs.graphqlJavaDataloader)
        //implementation(libs.bundles.slf4jBundle)
        //implementation(libs.logback)
        //implementation(libs.springSecurityRsa)
        implementation(libs.bundles.log4j)

        ktlintCfg(libs.bundles.ktlint)
    }
}

// aktuelle Snapshots laden
//configurations.all {
//    resolutionStrategy { cacheChangingModulesFor(0, "seconds") }
//}

allOpen { annotation("org.springframework.boot.context.properties.ConfigurationProperties") }
noArg { annotation("org.springframework.boot.context.properties.ConfigurationProperties") }

sweeney {
    enforce(mapOf("type" to "gradle", "expect" to "[7.3.1,7.3.1]"))
    // https://devcenter.heroku.com/articles/java-support#specifying-a-java-version
    enforce(mapOf("type" to "jdk", "expect" to "[16.0.2,17.0.1]"))
    validate()
}

tasks.compileJava {
    @Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS")
    targetCompatibility = JavaVersion.toVersion(System.getProperty("javaVersion"))?.majorVersion ?: libs.versions.javaVersion.get()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    // https://kotlinlang.org/docs/gradle.html#compiler-options
    kotlinOptions {
        apiVersion = "1.7"
        languageVersion = "1.7"

        jvmTarget = System.getProperty("javaVersion") ?: libs.versions.javaVersion.get()
        verbose = true
        freeCompilerArgs = listOfNotNull(
            "-Xjsr305=strict",
            "-progressive",
            "-Xsuppress-version-warnings",
            "-Xstring-concat=indy-with-constants"

            // TODO Inline-Klassen (statt Type Alias) funktionieren nicht mit Spring Data MongoDB, auch nicht mit CustomConversions
            //"-Xinline-classes"
        )

        //allWarningsAsErrors = true
        // ggf. wegen Kotlin-Daemon: %TEMP%\kotlin-daemon.* und %LOCALAPPDATA%\kotlin\daemon
        // https://youtrack.jetbrains.com/issue/KT-18300
        //  $env:LOCALAPPDATA\kotlin\daemon
        //  $env:TEMP\kotlin-daemon.<ZEITSTEMPEL>
    }
}

tasks.bootJar {
    doLast {
        println("")
        println("Aufruf der ausfuehrbaren JAR-Datei:")
        println("java -D'LOG_PATH=./build/log' -D'javax.net.ssl.trustStore=./src/main/resources/truststore.p12' -D'javax.net.ssl.trustStorePassword=zimmermann' -jar build/libs/${archiveFileName.get()} --spring.profiles.default=dev --spring.profiles.active=dev") // ktlint-disable max-line-length
        println("")
    }
}

// https://github.com/paketo-buildpacks/spring-boot
tasks.bootBuildImage {
    // "created 41 years ago" wegen Reproducability: https://medium.com/buildpacks/time-travel-with-pack-e0efd8bf05db

    // default:   imageName = "docker.io/${project.name}:${project.version}"
    val username = "juergenzimmermann"
    val tag = System.getProperty("tag") ?: project.version
    imageName = "docker.io/$username/${project.name}:$tag"

    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image.examples.builder-configuration
    // https://github.com/bell-sw/Liberica/releases
    // https://github.com/paketo-buildpacks/bellsoft-liberica/releases
    environment = mapOf(
        //"BP_JVM_VERSION" to "17.*"
        "BP_JVM_VERSION" to "17.0.1"
        //"BPL_JVM_THREAD_COUNT" to "50"

        //"BP_BOOT_NATIVE_IMAGE" to "true",
        // evtl. -H:+TraceClassInitialization
        //"BP_BOOT_NATIVE_IMAGE_BUILD_ARGUMENTS" to """
        //    --verbose
        //    -H:+ReportExceptionStackTraces
        //""".trimIndent()
    )
    // evtl. -H:+TraceClassInitialization
    // evtl. --initialize-at-build-time fuer Netty

    // Spring Native 0.11: Spring Boot 2.6, GraalVM 21.3, Java 17, Netty (NICHT: Tomcat), Gradle 7.x
    // https://github.com/paketo-buildpacks/spring-boot-native-image
    // https://github.com/paketo-buildpacks/builder
    // https://paketo.io/docs/getting-started/where-do-buildpacks-factor-in
    // https://github.com/spring-projects-experimental/spring-native
    // https://docs.spring.io/spring-native/docs/0.11.0-RC1/reference/htmlsingle/#support-spring-boot
    //builder = "paketobuildpacks/builder:tiny"

    // https://github.com/paketo-buildpacks/java-native-image/releases
    //buildpacks = listOf("gcr.io/paketo-buildpacks/java-native-image:5.12.0")
}

// Fuer Spring Native und logback.xml:
// https://docs.spring.io/spring-native/docs/0.11.0-RC1/reference/htmlsingle/#_starters_requiring_no_special_dependency_management
// https://docs.spring.io/spring-native/docs/0.11.0-RC1/reference/htmlsingle/#spring-aot
// https://github.com/spring-projects-experimental/spring-native/issues/625
// https://github.com/spring-projects/spring-boot/issues/25847
//springAot {
//    removeXmlSupport.set(false)
//}

// https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin
// https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md#why-is-my-image-created-48-years-ago
jib {
    val debug = System.getProperty("debug") ?: false.toString()

    from {
        // Cache fuer Images und Layers:   ${env:LOCALAPPDATA}\Local\Google\Jib\Cache

        // Ein "distroless image" enthaelt keine Package Manager, Shells, usw., sondern nur in der Variante -debug
        // d.h. ca. 250 MB statt ca. 450 MB
        // https://console.cloud.google.com/gcr/images/distroless
        image = "gcr.io/distroless/java-debian11:11"
        if (debug.toBoolean()) {
            image += "-debug"
        }
    }
    to {
        val username = "juergenzimmermann"
        val tag = System.getProperty("tag") ?: project.version
        image = "docker.io/$username/${project.name}:$tag"
        if (debug.toBoolean()) {
            image += "-debug"
        }
    }
    container {
        // User "nonroot" Group "nonroot", 1: root
        // siehe /etc/passwd
        user = "65532:65532"

        // Default: com.google.cloud.tools.jib.api.buildplan.ImageFormat.Docker
        //format = com.google.cloud.tools.jib.api.buildplan.ImageFormat.OCI

        //creationTime = "USE_CURRENT_TIMESTAMP"

        // https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md#jvm-flags
        jvmFlags = listOf("-Dspring.config.location=classpath:/application.yml")
    }

    skaffold.watch.setExcludes(setOf("extras"))

    // https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin#extended-usage Umgebungsvariable
    // https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin#system-properties
}

tasks.bootRun {
    // "System Properties", z.B. fuer Spring Properties oder fuer slf4j
    val port = System.getProperty("port")
    if (port != null) {
        systemProperty("server.port", port)
    }
    systemProperty("LOG_PATH", "./build/log")

    val hey = System.getProperty("hey")?.toLowerCase() == "true"
    if (hey) {
        systemProperty("APPLICATION_LOGLEVEL", "INFO")
        systemProperty("REQUEST_RESPONSE_LOGLEVEL", "INFO")
    } else {
        systemProperty("APPLICATION_LOGLEVEL", "TRACE")
    }

    args(
        "--spring.profiles.default=dev",
        "--spring.profiles.active=dev",
        "--spring.output.ansi.enabled=ALWAYS",
        "--spring.config.location=classpath:/application.yml",
        "--spring.data.mongodb.password=p",
        "--spring.devtools.restart.enabled=true",
        "--spring.devtools.restart.trigger-file=.reloadtrigger"
    )

    // Hotspot Compiler fuer aggressivere Optimierung als der Client-Compiler, aber laengere Startzeit: -server"
    // Remote Debugger:   .\gradlew bootRun --debug-jvm -verbose:class -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005
}

tasks.test {
    useJUnitPlatform {}

    val fork = System.getProperty("fork") ?: "1"
    maxParallelForks = fork.toInt()

    systemProperty("javax.net.ssl.trustStore", "./src/main/resources/truststore.p12")
    systemProperty("javax.net.ssl.trustStorePassword", "zimmermann")
    systemProperty("junit.platform.output.capture.stdout", true)
    systemProperty("junit.platform.output.capture.stderr", true)
    systemProperty("spring.config.location", "./src/main/resources/application.yml")
    systemProperty("spring.data.mongodb.password", "p")
    // Tests ohne TLS und ohne HTTP2
    systemProperty("server.ssl.enabled", false)
    systemProperty("server.http2.enabled", false)

    // Umgebungsvariable, z.B. fuer Spring Properties, slf4j oder WebClient
    environment("LOG_PATH", "./build/log")
    environment("APPLICATION_LOGLEVEL", "TRACE")
    // Warning beim Ende der einzelnen Tests unterdruecken
    environment("WEBAPP_CLASS_LOADER_BASE_LOGLEVEL", "ERROR")

    // https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
    // https://www.jetbrains.com/help/idea/run-debug-configuration-junit.html
    // https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
    //debug = true

    //finalizedBy("jacocoTestReport")
}

// https://docs.qameta.io/allure/#_gradle_2
allure {
    version.set(libs.versions.allure.get())
    adapter {
        frameworks {
            junit5 {
                adapterVersion.set(libs.versions.allureJunit.get())
                autoconfigureListeners.set(true)
                enabled.set(true)
            }
        }
        autoconfigure.set(true)
        aspectjWeaver.set(false)
        aspectjVersion.set(libs.versions.aspectjweaver.get())
    }

    //downloadLink = "https://repo1.maven.org/maven2/io/qameta/allure/allure-commandline/${libs.versions.allureCommandline.get()}/allure-commandline-${libs.versions.allureCommandline.get()}.zip"
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

// https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/#configuring-tasks
tasks.getByName<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // afterEvaluate gibt es nur bei getByName<> ("eager"), nicht bei named<> ("lazy")
    // https://docs.gradle.org/5.0/release-notes.html#configuration-avoidance-api-disallows-common-configuration-errors
    afterEvaluate {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) { exclude("**/config/**", "**/entity/**") }
                }
            )
        )
    }

    // https://github.com/gradle/gradle/pull/12626
    dependsOn(tasks.test)
}

// https://ktlint.github.io/#getting-started
// https://android.github.io/kotlin-guides/style.html
// https://kotlinlang.org/docs/reference/coding-conventions.html
// https://www.jetbrains.com/help/idea/code-style-kotlin.html
// https://github.com/android/kotlin-guides/issues/37
// https://github.com/shyiko/ktlint
@Suppress("KDocMissingDocumentation")
val ktlint by tasks.register<JavaExec>("ktlint") {
    group = "verification"

    classpath = ktlintCfg
    mainClass.set("com.pinterest.ktlint.Main")
    // https://github.com/pinterest/ktlint/blob/master/ktlint/src/main/kotlin/com/pinterest/ktlint/Main.kt
    args = listOfNotNull(
        "--verbose",
        "--experimental",
        "--relative",
        "--color",
        "--reporter=plain",
        "--reporter=checkstyle,output=$buildDir/reports/ktlint.xml",
        "src/**/*.kt"
    )
}
tasks.check { dependsOn(ktlint) }

detekt {
    buildUponDefaultConfig = true
    allRules = true
    parallel = true
    config = files(project.rootDir.resolve("extras/detekt.yaml"))
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        val reportsDir = "$buildDir/reports"
        xml.outputLocation.set(file("$reportsDir/detekt.xml"))
        html.outputLocation.set(file("$reportsDir/detekt.html"))
        txt.required.set(false)
    }
}

// https://github.com/jeremylong/DependencyCheck/blob/master/src/site/markdown/dependency-check-gradle/configuration.md
dependencyCheck {
    scanConfigurations = listOfNotNull("runtimeClasspath")
    suppressionFile = "$projectDir/extras/owasp.xml"
    data(
        closureOf<org.owasp.dependencycheck.gradle.extension.DataExtension> {
            directory = "C:/Zimmermann/owasp-dependency-check"
            username = "dc"
            password = "p"
        }
    )

    analyzedTypes = listOfNotNull("jar")
    analyzers(
        closureOf<org.owasp.dependencycheck.gradle.extension.AnalyzerExtension> {
            // nicht benutzte Analyzer
            assemblyEnabled = false
            autoconfEnabled = false
            bundleAuditEnabled = false
            cmakeEnabled = false
            cocoapodsEnabled = false
            composerEnabled = false
            golangDepEnabled = false
            golangModEnabled = false
            nodeEnabled = false
            nugetconfEnabled = false
            nuspecEnabled = false
            pyDistributionEnabled = false
            pyPackageEnabled = false
            rubygemsEnabled = false
            swiftEnabled = false

            nodeAudit(closureOf<org.owasp.dependencycheck.gradle.extension.NodeAuditExtension> { enabled = true })
            retirejs(closureOf<org.owasp.dependencycheck.gradle.extension.RetireJSExtension> { enabled = true })
            //ossIndex(closureOf<org.owasp.dependencycheck.gradle.extension.OssIndexExtension> { enabled = true })
        }
    )

    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
}

snyk {
    setArguments("--configuration-matching=implementation|runtimeOnly")
    setSeverity("low")
    setApi("40df2078-e1a3-4f28-b913-e2babbe427fd")
}

// SVG-Dateien von AsciidoctorPdf fuer Dokka umkopieren
@Suppress("KDocMissingDocumentation")
val copySvg by tasks.register<Copy>("copySvg") {
    from("$buildDir/docs/asciidocPdf")
    include("*.svg")
    into("$buildDir/dokka/html/images")

    dependsOn("asciidoctorPdf")
}

// https://github.com/Kotlin/dokka/blob/master/docs/src/doc/docs/user_guide/gradle/usage.md
tasks.dokkaHtml {
    // default: $USER_HOME/.cache/dokka
    cacheRoot.set(file("$buildDir/dokka/cache"))
    dokkaSourceSets {
        configureEach {
            includes.from("Module.md")
            reportUndocumented.set(true)
            jdkVersion.set(libs.versions.javaVersion.get().toInt())
            noStdlibLink.set(true)
            noJdkLink.set(true)
            failOnWarning.set(true)
        }
    }

    dependsOn("createDokkaCacheDirectory", copySvg)
}

tasks.register("createDokkaCacheDirectory") {
    doLast { mkdir("$buildDir/dokka/cache") }
}

tasks.asciidoctor {
    asciidoctorj {
        setVersion(libs.versions.asciidoctorj.get())
        //requires("asciidoctor-diagram")

        modules {
            diagram.use()
            diagram.setVersion(libs.versions.asciidoctorjDiagram.get())
        }
    }

    val separator = System.getProperty("file.separator")
    setBaseDir(file("extras${separator}doc"))
    setSourceDir(file("extras${separator}doc"))
    //setOutputDir(file("$buildDir/docs/asciidoc"))
    logDocuments = true

    // https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/597#issuecomment-844352804
    inProcess = org.asciidoctor.gradle.base.process.ProcessMode.JAVA_EXEC
    forkOptions {
        jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.base/java.io=ALL-UNNAMED")
    }

    doLast {
        println("Das Entwicklerhandbuch ist in $buildDir${separator}extras${separator}docs${separator}asciidoc${separator}entwicklerhandbuch.html") // ktlint-disable max-line-length
    }
}

tasks.asciidoctorPdf {
    asciidoctorj {
        setVersion(libs.versions.asciidoctorj.get())

        modules {
            diagram.use()
            diagram.setVersion(libs.versions.asciidoctorjDiagram.get())
            pdf.setVersion(libs.versions.asciidoctorjPdf.get())
        }
    }

    val separator = System.getProperty("file.separator")
    setBaseDir(file("extras${separator}doc"))
    setSourceDir(file("extras${separator}doc"))
    attributes(mapOf("pdf-page-size" to "A4"))
    logDocuments = true

    // https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/597#issuecomment-844352804
    inProcess = org.asciidoctor.gradle.base.process.ProcessMode.JAVA_EXEC
    forkOptions {
        jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.base/java.io=ALL-UNNAMED")
    }

    doLast {
        println("Das Entwicklerhandbuch ist in $buildDir${separator}docs${separator}asciidocPdf${separator}entwicklerhandbuch.pdf") // ktlint-disable max-line-length
    }
}

// jig {
//     // Output all elements regardless of package
//     modelPattern = ".+"
//     // Output only package related diagram and business rule list
//     //documentTypes = listOf("PackageRelationDiagram", "BusinessRuleList")
// }

licenseReport {
    configurations = arrayOf("runtimeClasspath")
}

tasks.dependencyUpdates {
    checkConstraints = true
}

idea {
    module {
        isDownloadJavadoc = true
    }
}
