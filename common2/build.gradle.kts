import com.android.build.api.dsl.KotlinMultiplatformAndroidCompilation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("mylib.convention")
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "common2"
        compileSdk = 36
        minSdk = 26
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("commonJvmAndroid") {
                withJvm()
                withCompilations { it is KotlinMultiplatformAndroidCompilation }
            }
        }
    }
}
