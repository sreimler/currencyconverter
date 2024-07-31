import com.android.build.api.dsl.ApplicationExtension
import com.sreimler.currencyconverter.convention.ExtensionType
import com.sreimler.currencyconverter.convention.configureAndroidCompose
import com.sreimler.currencyconverter.convention.configureBuildTypes
import com.sreimler.currencyconverter.convention.configureKotlinAndroid
import com.sreimler.currencyconverter.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            // Equivalent to plugins block in build.gradle
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            // Equivalent to android block in build.gradle
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                }

                configureKotlinAndroid(this)
                configureBuildTypes(this, ExtensionType.APPLICATION)
            }

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}