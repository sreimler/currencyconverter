plugins {
    alias(libs.plugins.currencyconverter.android.library)
}

android {
    namespace = "com.sreimler.currencyconverter.list.data"
}

dependencies {
    implementation(projects.list.domain)
    implementation(projects.core.domain)
    implementation(projects.core.data)
}