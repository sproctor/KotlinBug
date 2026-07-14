package bug

// From a plain Maven JAR: com.google.zxing:core (packaging = jar).
// BUG: this import is flagged "Unresolved reference" (red) in IntelliJ IDEA and
// Android Studio, even though `./gradlew :common:compileAndroidMain` compiles it fine.
import com.google.zxing.BarcodeFormat

// From an AAR: androidx.core:core (packaging = aar), declared in the SAME androidMain
// source set of this com.android.kotlin.multiplatform.library module. This import
// resolves correctly in the IDE. Control case that isolates the difference to jar vs aar.
import androidx.core.graphics.ColorUtils

@Suppress("unused")
val fromPlainJar: BarcodeFormat = BarcodeFormat.QR_CODE

@Suppress("unused")
val fromAar: Class<*> = ColorUtils::class.java
