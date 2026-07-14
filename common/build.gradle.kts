plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "common"
        compileSdk = 36
        minSdk = 26
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.collections.immutable)

                api(libs.kotlinx.io.core)
            }
        }
        androidMain {
            dependencies {
                // Plain Maven JAR (packaging=jar). `./gradlew :common:compileAndroidMain`
                // compiles fine, but its imports are UNRESOLVED in the IDE. <-- the bug
                implementation(libs.zxing.core)
                // AAR (packaging=aar) in the SAME source set — resolves in the IDE. Control.
                implementation(libs.androidx.core)
            }
        }
    }
}
