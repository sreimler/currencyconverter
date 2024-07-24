import com.android.build.api.dsl.LibraryExtension
import com.sreimler.currencyconverter.convention.configureAndroidCompose
import com.sreimler.currencyconverter.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.sreimler.currencyconverter.android.library")

                val extension = extensions.getByType<LibraryExtension>()
                configureAndroidCompose(extension)
            }

            dependencies {
                "implementation"(project.libs.findBundle("koin-compose").get())
                "implementation"(project.libs.findBundle("compose").get())
                "debugImplementation"(project.libs.findBundle("compose-debug").get())
                "androidTestImplementation"(project.libs.findLibrary("androidx-ui-test-junit4").get())
            }
        }
    }
}