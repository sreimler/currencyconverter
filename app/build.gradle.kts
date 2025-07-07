plugins {
    alias(libs.plugins.currencyconverter.android.application)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

// Version information
val versionMajor = 0
val versionMinor = 8
val versionPatch = 1

android {
    signingConfigs {
        create("release") {
            storeFile = file(providers.gradleProperty("storeFile").get())
            storePassword = providers.gradleProperty("storePassword").get()
            keyAlias = providers.gradleProperty("keyAlias").get()
            keyPassword = providers.gradleProperty("keyPassword").get()
        }
    }
    namespace = "com.sreimler.currencyconverter"

    defaultConfig {
        applicationId = "com.sreimler.currencyconverter"
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.presentation)
    implementation(projects.core.database)

    implementation(projects.rates.domain)
    implementation(projects.rates.data)
    implementation(projects.rates.presentation)

    implementation(projects.converter.domain)
    implementation(projects.converter.data)
    implementation(projects.converter.presentation)

    // Core
    implementation(libs.androidx.core.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Compose
    implementation(libs.androidx.material3) // TODO: Remove when settings have been refactored into a module

    // Koin DI
    implementation(libs.bundles.koin)

    // Preferences DataStore
    implementation(libs.androidx.datastore)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.analytics)

    // Timber logging
    implementation(libs.timber)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}