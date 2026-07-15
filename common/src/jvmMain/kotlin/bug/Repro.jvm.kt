package bug

import androidx.compose.ui.graphics.ImageBitmap
import com.google.zxing.BarcodeFormat

actual object ReproRenderer {
    actual fun render(): ImageBitmap? {
        BarcodeFormat.QR_CODE
        return null
    }
}
