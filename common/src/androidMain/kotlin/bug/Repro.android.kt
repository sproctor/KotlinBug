package bug

import androidx.compose.ui.graphics.ImageBitmap

// android `actual object`. From a plain Maven JAR (com.google.zxing:core, packaging=jar).
// BUG: this import is "Unresolved reference" (red) in the IDE, though the CLI compiles it.
import com.google.zxing.BarcodeFormat

// From an AAR (androidx.core:core, packaging=aar), android-only. Resolves in the IDE. Control.
import androidx.core.graphics.ColorUtils

// A type from the :common2 project dependency.
import bug2.Thing

actual object ReproRenderer {
    actual fun render(): ImageBitmap? {
        BarcodeFormat.QR_CODE
        ColorUtils::class.java
        Thing()
        return null
    }
}
