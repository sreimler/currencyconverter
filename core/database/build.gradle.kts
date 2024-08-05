plugins {
    alias(libs.plugins.currencyconverter.android.library)
    alias(libs.plugins.currencyconverter.android.room)
}

android {
    namespace = "com.sreimler.currencyconverter.core.database"
}

dependencies {
    implementation(projects.core.domain)

    // di
    implementation(libs.bundles.koin)
}