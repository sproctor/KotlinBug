import org.gradle.api.Plugin
import org.gradle.api.Project

// Mirrors the real project's convention plugin: applies the KMP + Android-KMP-library
// plugins by id. Its role in this repro is to make the build a Gradle *composite build*
// via settings.gradle.kts `pluginManagement { includeBuild("build-logic") }`, which is the
// factor the standalone (non-composite) repro was missing.
class MyLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.kotlin.multiplatform.library")
            apply("com.google.devtools.ksp")
        }
    }
}
