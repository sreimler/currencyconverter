plugins {
    alias(libs.plugins.currencyconverter.android.library)
}

android {
    namespace = "com.sreimler.currencyconverter.converter.data"
}

dependencies {
    implementation(projects.converter.domain)
    implementation(projects.core.domain)
    implementation(projects.core.data)
}