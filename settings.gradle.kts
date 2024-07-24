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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Currency Converter"
include(":app")
include(":converter:domain")
include(":converter:data")
include(":converter:presentation")
include(":list:domain")
include(":list:data")
include(":list:presentation")
include(":core:database")
include(":build-logic:convention")
