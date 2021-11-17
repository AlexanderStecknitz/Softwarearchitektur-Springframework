pluginManagement {
    repositories {
        gradlePluginPortal()
        //maven("https://plugins.gradle.org/m2")
        mavenCentral()

        maven("https://repo.spring.io/milestone")

        // Snapshots von Spring Framework, Spring Data, Spring Security und Spring Cloud
        //maven("https://repo.spring.io/snapshot") { mavenContent { snapshotsOnly() } }
        //maven("https://repo.spring.io/plugins-release")
    }
}

//buildCache {
//    local {
//        directory = "C:/Z/caches"
//    }
//}

enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "artikel"
