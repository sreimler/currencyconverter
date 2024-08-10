plugins {
    alias(libs.plugins.currencyconverter.android.library)
}

android {
    namespace = "com.sreimler.currencyconverter.core.data"
}

dependencies {
    implementation(projects.core.domain)

    // Koin DI
    implementation(libs.bundles.koin)

    // Preferences DataStore
    implementation(libs.androidx.datastore)

    // Retrofit
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
}