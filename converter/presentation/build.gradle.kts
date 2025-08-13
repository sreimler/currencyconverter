plugins {
    alias(libs.plugins.currencyconverter.android.library.compose)
}

android {
    namespace = "com.sreimler.currencyconverter.converter.presentation"
}

dependencies {
    implementation(projects.converter.domain)
    implementation(projects.core.domain)
    implementation(projects.core.presentation)
}
