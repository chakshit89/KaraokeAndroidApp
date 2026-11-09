pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()

        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://jitpack.io")
                }
                maven {
                    name = "TarsosDSP"
                    url = uri("https://mvn.0110.be/releases")
                }
            }
            filter {
                includeGroup("be.tarsos.dsp")
            }
        }
    }
}

rootProject.name = "MyOyaSing"
include(":app")
 