pluginManagement {
    includeBuild("build-logic")
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "CurrencyConverter"
include(":app")
include(":converter:domain")
include(":converter:data")
include(":converter:presentation")
include(":list:domain")
include(":list:data")
include(":list:presentation")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":core:network")
