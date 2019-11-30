package jp.co.soramitsu.irohaandroid.util

import android.graphics.Bitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder

class QrCodeGenerator(
    private val firstColor: Int,
    private val secondColor: Int
) {

    companion object {
        private const val RECEIVE_QR_SCALE_SIZE = 1024
        private const val PADDING_SIZE = 2
    }

    fun generateQrBitmap(input: String): Bitmap {
        val hints = HashMap<EncodeHintType, String>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val qrCode = Encoder.encode(input, ErrorCorrectionLevel.H, hints)
        val byteMatrix = qrCode.matrix
        val width = byteMatrix.width + PADDING_SIZE
        val height = byteMatrix.height + PADDING_SIZE
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (y == 0 || y > byteMatrix.height || x == 0 || x > byteMatrix.width) {
                    bitmap.setPixel(x, y, secondColor)
                } else {
                    bitmap.setPixel(x, y, if (byteMatrix.get(x - PADDING_SIZE / 2, y - PADDING_SIZE / 2).toInt() == 1) firstColor else secondColor)
                }
            }
        }
        return Bitmap.createScaledBitmap(bitmap, RECEIVE_QR_SCALE_SIZE, RECEIVE_QR_SCALE_SIZE, false)
    }
}