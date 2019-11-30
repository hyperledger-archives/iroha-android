package jp.co.soramitsu.irohaandroid.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import io.reactivex.Single

class QrCodeDecoder(
    private val contentResolver: ContentResolver
) {

    fun decodeQrFromUri(data: Uri): Single<String> {
        return decode(data)
            .onErrorResumeNext { Single.error(Throwable()) }
    }

    private fun decode(data: Uri): Single<String> {
        return Single.create {
            val qrBitmap = MediaStore.Images.Media.getBitmap(contentResolver, data)

            val pixels = IntArray(qrBitmap.height * qrBitmap.width)
            qrBitmap.getPixels(pixels, 0, qrBitmap.width, 0, 0, qrBitmap.width, qrBitmap.height)
            qrBitmap.recycle()
            val source = RGBLuminanceSource(qrBitmap.width, qrBitmap.height, pixels)
            val bBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()

            val textResult = reader.decode(bBitmap).text

            if (textResult.isNullOrEmpty()) {
                it.onError(Throwable())
            } else {
                it.onSuccess(textResult)
            }
        }
    }
}