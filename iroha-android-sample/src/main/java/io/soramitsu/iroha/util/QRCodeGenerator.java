/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
    public static final String ENCODE_CHARACTER_TYPE_ISO_8859_1 = "ISO-8859-1";
    public static final String ENCODE_CHARACTER_TYPE_UTF_8 = "UTF-8";

    private QRCodeGenerator() {
    }

    public static Bitmap generateQR(String qrText, int size, String encodeCharacterSet) throws WriterException {
        Map<EncodeHintType,String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, encodeCharacterSet);
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