plugins {
    alias(libs.plugins.currencyconverter.android.library.compose)
}

android {
    namespace = "com.sreimler.currencyconverter.core.presentation"
}

dependencies {
    implementation(projects.core.domain)
}