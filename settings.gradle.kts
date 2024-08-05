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

// Temporary fix for "Unable to make progress running work" issue: https://issuetracker.google.com/issues/315023802
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "CurrencyConverter"
include(":app")
include(":converter:domain")
include(":converter:data")
include(":converter:presentation")
include(":list:domain")
include(":list:data")
include(":list:presentation")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":core:database")
