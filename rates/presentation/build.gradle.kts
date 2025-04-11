plugins {
    alias(libs.plugins.currencyconverter.android.library.compose)
}

android {
    namespace = "com.sreimler.currencyconverter.list.presentation"
}

dependencies {
    implementation(projects.rates.domain)
    implementation(projects.core.domain)
    implementation(projects.core.presentation)
}