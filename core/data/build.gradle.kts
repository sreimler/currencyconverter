plugins {
    alias(libs.plugins.currencyconverter.android.library)
}

android {
    namespace = "com.sreimler.currencyconverter.core.data"
}

dependencies {
    implementation(projects.core.domain)
}