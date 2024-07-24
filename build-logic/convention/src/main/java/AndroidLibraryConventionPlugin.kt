import com.android.build.api.dsl.LibraryExtension
import com.sreimler.currencyconverter.convention.ExtensionType
import com.sreimler.currencyconverter.convention.configureBuildTypes
import com.sreimler.currencyconverter.convention.configureKotlinAndroid
import com.sreimler.currencyconverter.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            // Equivalent to plugins block in build.gradle
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureBuildTypes(this, ExtensionType.LIBRARY)

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            dependencies {
                "testImplementation"(kotlin("test"))
                "testImplementation"(project.libs.findLibrary("junit").get())
                "androidTestImplementation"(project.libs.findLibrary("junit").get())
                "androidTestImplementation"(project.libs.findLibrary("androidx-junit").get())
                "androidTestImplementation"(project.libs.findLibrary("androidx-espresso-core").get())
            }
        }
    }
}