plugins {
    alias(libs.plugins.currencyconverter.android.library)
}

android {
    namespace = "com.sreimler.currencyconverter.rates.data"
}

dependencies {
    implementation(projects.rates.domain)
    implementation(projects.core.domain)
    implementation(projects.core.data)
}