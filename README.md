# Repro: `kermit` + an `expect object` make plain-JAR imports unresolved in the IDE (`com.android.kotlin.multiplatform.library`)

## Summary

In a module using `com.android.kotlin.multiplatform.library`, when the module **both**:
1. contains an **`expect object`** (in `commonMain`), and
2. depends on **`co.touchlab:kermit`**,

then **every plain Maven JAR import** in the module's `androidMain`/`jvmMain` shows as
**`Unresolved reference`** in the IDE (IntelliJ IDEA / Android Studio), even though the Gradle
build compiles fine. **AAR** dependencies in the same source set keep resolving.

Neither condition alone triggers it:
- kermit present but no `expect object` → resolves fine.
- `expect object` present but no kermit → resolves fine.
- both → all jar imports in the module go red (whole-module effect — even an unrelated
  top-level `val` importing a jar goes red).

Likely-relevant detail: `kermit` publishes **separate debug/release Android library variants**
(`kermit-android` + `kermit-android-debug`, the `publishLibraryVariants("release","debug")`
pattern), unlike e.g. `kotlinx-coroutines-core` (a KMP dep that does **not** trigger this).

Not a Gradle composite build issue (distinct from
[KTIJ-37107](https://youtrack.jetbrains.com/issue/KTIJ-37107)); AARs resolve fine.

## Where to look

`common/src/androidMain/kotlin/bug/Repro.android.kt`:

```kotlin
import com.google.zxing.BarcodeFormat        // com.google.zxing:core  (JAR) -> unresolved in IDE
import androidx.core.graphics.ColorUtils      // androidx.core:core     (AAR) -> resolves
```

- `common/src/commonMain/kotlin/bug/Repro.kt` — the `expect object ReproRenderer`.
- `common/build.gradle.kts` — `implementation(libs.kermit)` is the trigger. **Comment it out and
  the zxing import resolves again.**

## Steps

1. Open the project; let the Gradle import finish.
2. Open `Repro.android.kt`; the zxing import is red.
3. Remove `implementation(libs.kermit)` from `common/build.gradle.kts`, re-sync → it resolves.

## Build succeeds

```
./gradlew :common:compileAndroidMain :common:compileKotlinJvm
```

compiles both imports — the problem is IDE analysis only.

## Environment

- AGP 9.0.1, Kotlin 2.4.0, Gradle 9.5.1, Compose 1.11.1, kermit 2.1.0, compileSdk 36.
- IDE: `<fill in from Help > About>`.
