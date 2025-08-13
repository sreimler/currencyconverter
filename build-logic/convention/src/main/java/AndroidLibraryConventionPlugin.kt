import com.android.build.api.dsl.LibraryExtension
import com.sreimler.currencyconverter.convention.ExtensionType
import com.sreimler.currencyconverter.convention.configureBuildTypes
import com.sreimler.currencyconverter.convention.configureKotlinAndroid
import com.sreimler.currencyconverter.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            // Equivalent to plugins block in build.gradle
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureBuildTypes(this, ExtensionType.LIBRARY)

                // Set SDK versions here for all library modules
                compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()
                defaultConfig {
                    minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                testOptions {
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                }
                lint {
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                }
            }

            dependencies {
                "implementation"(project.libs.findLibrary("kotlinx-serialization-json").get())
                "implementation"(project.libs.findLibrary("timber").get())
                "testImplementation"(project.libs.findLibrary("junit").get())
                "testImplementation"(project.libs.findLibrary("mockk-android").get())
                "testImplementation"(project.libs.findLibrary("mockk-agent").get())
                "testImplementation"(project.libs.findLibrary("kotlinx-coroutines-test").get())
                "testImplementation"(project.libs.findLibrary("turbine").get())
                "testImplementation"(libs.findLibrary("timber").get())
                "androidTestImplementation"(project.libs.findLibrary("junit").get())
                "androidTestImplementation"(project.libs.findLibrary("androidx-junit").get())
                "androidTestImplementation"(project.libs.findLibrary("androidx-espresso-core").get())
            }
        }
    }
}
