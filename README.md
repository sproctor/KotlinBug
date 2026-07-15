# Repro: plain Maven JAR unresolved in the IDE for a `com.android.kotlin.multiplatform.library` module with an `expect object`

## Summary

In a module using `com.android.kotlin.multiplatform.library`, once the module contains an
**`expect object`** (in `commonMain`) whose `actual` (in `androidMain`) imports a plain Maven
**JAR** dependency, that jar — and in fact **every** jar import across the module's
androidMain/jvmMain — shows as **`Unresolved reference`** in the IDE, while the Gradle build
compiles fine. AAR dependencies in the same source set continue to resolve.

Key details established while narrowing this down:
- An `expect fun` does **not** trigger it; an **`expect object`** does.
- Adding the `expect object` flips the whole module: even a plain top-level `val` elsewhere in
  androidMain that imported the same jar (and resolved fine) goes red once the `expect object`
  is present.
- The real project's construct is `expect object ReceiptRenderer { fun render(...): ImageBitmap? }`
  (return type is a Compose type, itself an `expect class`). This repro mirrors that with
  `expect object ReproRenderer { fun render(): ImageBitmap? }`.
- It is **not** a Gradle composite build issue (distinct from
  [KTIJ-37107](https://youtrack.jetbrains.com/issue/KTIJ-37107)): AARs resolve fine.

## Where to look

`common/src/androidMain/kotlin/bug/Repro.android.kt`:

```kotlin
import com.google.zxing.BarcodeFormat        // com.google.zxing:core  (JAR) -> unresolved in IDE
import androidx.core.graphics.ColorUtils      // androidx.core:core     (AAR) -> resolves
```

`common/src/commonMain/kotlin/bug/Repro.kt` holds the `expect object ReproRenderer`.

## Steps

1. Open the project in IntelliJ IDEA / Android Studio; let Gradle import finish.
2. Open `Repro.android.kt` and observe the zxing import.

## Expected vs actual

- Expected: both imports resolve (the build compiles both).
- Actual: the JAR import is unresolved (red); the AAR import resolves.

## Build succeeds

```
./gradlew :common:compileAndroidMain :common:compileKotlinJvm
```

compiles cleanly — the problem is IDE analysis only.

## Environment

- AGP 9.0.1, Kotlin 2.4.0, Gradle 9.5.1, Compose 1.11.1, KSP 2.3.10, compileSdk 36.
- IDE: `<fill in from Help > About>`.

## Status note

This standalone project mirrors the exact shape that was confirmed to reproduce inside a larger
multi-module project. If a fresh clone does **not** show the red import, the trigger may
additionally require the surrounding multi-module context; the commit history records the
narrowing steps.
