# Repro: plain Maven JAR in `androidMain` is unresolved in the IDE for a `com.android.kotlin.multiplatform.library` module

## Summary

In a module using the `com.android.kotlin.multiplatform.library` plugin, a plain Maven
**JAR** dependency (`packaging=jar`) declared in the `androidMain` source set is reported
as **`Unresolved reference` in the IDE** (IntelliJ IDEA / Android Studio), even though the
Gradle build compiles it successfully. An **AAR** dependency (`packaging=aar`) in the *same*
source set resolves correctly. The only difference between the two is the artifact
packaging, which isolates the problem to how the IDE attaches jar (non-AAR) dependencies to
an Android KMP source set's analysis classpath.

This is **not** [KTIJ-37107](https://youtrack.jetbrains.com/issue/KTIJ-37107): this project is
**not** a Gradle composite build (no `includeBuild`), and Android-library (AAR) dependencies
resolve fine here — it is the plain JARs that don't.

## Where to look

[`common/src/androidMain/kotlin/bug/JarVsAarRepro.kt`](common/src/androidMain/kotlin/bug/JarVsAarRepro.kt):

```kotlin
import com.google.zxing.BarcodeFormat        // from com.google.zxing:core  (JAR) -> RED in IDE
import androidx.core.graphics.ColorUtils      // from androidx.core:core     (AAR) -> resolves
```

Both are declared in `common`'s `androidMain` (see [`common/build.gradle.kts`](common/build.gradle.kts)),
which applies `com.android.kotlin.multiplatform.library`.

## Steps to reproduce

1. Open this project in IntelliJ IDEA or Android Studio and let the Gradle import finish.
2. Open `common/src/androidMain/kotlin/bug/JarVsAarRepro.kt`.

## Expected

Both imports resolve (both dependencies are on the `androidMain` compile classpath, and the
Gradle build compiles the file).

## Actual

- `import com.google.zxing.BarcodeFormat` (from the plain **JAR**) → **`Unresolved reference`** (red).
- `import androidx.core.graphics.ColorUtils` (from the **AAR**) → resolves normally.

Invalidate Caches / Restart does not help.

## The build succeeds

```
./gradlew :common:compileAndroidMain
```

compiles `JarVsAarRepro.kt` (both imports) successfully. The problem is IDE-only analysis.

## Environment

- AGP 9.0.1, Kotlin 2.4.0, Gradle 9.5.1, compileSdk 36, minSdk 26 — the same versions as the real project where this was first observed.
- Single-module project (`common`) — no composite build, no app module.
- IDE: `<fill in from Help > About — e.g. IntelliJ IDEA 2026.1 / Android Studio Panda; include the Kotlin plugin / K2 analyzer version>`.
