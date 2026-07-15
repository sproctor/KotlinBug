package bug

import androidx.compose.ui.graphics.ImageBitmap

// `expect object` returning a Compose ImageBitmap?, matching core:print's
// `expect object ReceiptRenderer { fun render(...): ImageBitmap? }` exactly. The android
// `actual object` imports a plain jar (zxing) that then shows unresolved in the IDE.
expect object ReproRenderer {
    fun render(): ImageBitmap?
}
