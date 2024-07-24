plugins {
    alias(libs.plugins.currencyconverter.android.library)
}

android {
    namespace = "com.sreimler.currencyconverter.core.database"
}

dependencies {
    implementation(projects.core.domain)
}