plugins {
    alias(libs.plugins.currencyconverter.android.application)
}

// Version information
val versionMajor = 0
val versionMinor = 2
val versionPatch = 2

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
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.presentation)
    implementation(projects.core.database)

    implementation(projects.list.domain)
    implementation(projects.list.data)
    implementation(projects.list.presentation)

    implementation(projects.converter.domain)
    implementation(projects.converter.data)
    implementation(projects.converter.presentation)

    // Core
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // Koin DI
    implementation(libs.bundles.koin)

    // Preferences DataStore
    implementation(libs.androidx.datastore)

    // Timber logging
    implementation(libs.timber)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}