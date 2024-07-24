package com.sreimler.currencyconverter.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File
import java.util.Properties

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>, extensionType: ExtensionType
) {
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }

        // Load properties file for API keys
        val apiProperties = Properties().apply {
            File(rootProject.projectDir, "apikeys.properties").inputStream().use { load(it) }
        }
        // Retrieve the API key
        val apiKey = apiProperties["API_KEY_FREECURRENCY"] as String

        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey)
                        }
                    }
                }
            }

            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey)
                        }
                    }
                }
            }
        }

    }
}

private fun BuildType.configureDebugBuildType(apiKey: String) {
    setBuildConfigFields(apiKey)
}

private fun BuildType.configureReleaseBuildType(commonExtension: CommonExtension<*, *, *, *, *, *>, apiKey: String) {
    setBuildConfigFields(apiKey)

    isMinifyEnabled = true
    proguardFiles(commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
}

private fun BuildType.setBuildConfigFields(apiKey: String) {
    buildConfigField("String", "API_KEY_FREECURRENCY", "\"$apiKey\"")
}