import com.android.build.api.dsl.KotlinMultiplatformAndroidCompilation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    // Applied via a convention plugin from the included build `build-logic`, which makes
    // this a Gradle composite build (see settings.gradle.kts). The convention plugin
    // applies org.jetbrains.kotlin.multiplatform + com.android.kotlin.multiplatform.library.
    id("mylib.convention")
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "common"
        compileSdk = 36
        minSdk = 26
        // core:print's convention enables this; matching it here.
        androidResources {
            enable = true
        }
    }

    // Intermediate source set shared by the jvm + android leaves, matching the real project:
    // commonMain -> commonJvmAndroidMain -> androidMain / jvmMain.
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("commonJvmAndroid") {
                withJvm()
                withCompilations { it is KotlinMultiplatformAndroidCompilation }
            }
        }
    }

    // Needed for `expect object` (expect/actual classes), as in the real project.
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common2"))
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.collections.immutable)
                api(libs.kotlinx.io.core)
                api(libs.kotlin.inject.runtime)
                implementation(libs.compose.ui)
            }
        }
        androidMain {
            dependencies {
                // Plain Maven JAR (packaging=jar), declared on BOTH the android and jvm
                // leaves (as in the real project). `./gradlew :common:compileAndroidMain`
                // compiles fine, but its imports are UNRESOLVED in the IDE. <-- the bug
                implementation(libs.zxing.core)
                // AAR (packaging=aar), android-only — resolves in the IDE. Control.
                implementation(libs.androidx.core)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.zxing.core)
            }
        }
    }
}

// KSP processor (kotlin-inject) on the android + jvm leaves, mirroring the real project's
// scrapgolem.inject convention plugin.
dependencies {
    add("kspAndroid", libs.kotlin.inject.compiler.ksp)
    add("kspJvm", libs.kotlin.inject.compiler.ksp)
}
