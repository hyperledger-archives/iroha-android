package io.soramitsu.iroha.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    public static Bitmap generateQR(String qrText, int size) throws WriterException {
        Map<EncodeHintType,String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        QRCode qrCode = Encoder.encode(qrText, ErrorCorrectionLevel.H, hints);
        ByteMatrix byteMatrix = qrCode.getMatrix();
        Bitmap bitmap = Bitmap.createBitmap(byteMatrix.getWidth(), byteMatrix.getHeight(), Bitmap.Config.ARGB_8888);
        for (int y = 0; y < byteMatrix.getHeight(); ++y) {
            for (int x = 0; x < byteMatrix.getWidth(); ++x) {
                byte val = byteMatrix.get(x, y);
                bitmap.setPixel(x, y, val == 1 ? Color.BLACK : Color.WHITE);
            }
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        return bitmap;
    }
}