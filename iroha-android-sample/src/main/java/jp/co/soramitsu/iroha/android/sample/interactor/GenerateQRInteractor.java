package jp.co.soramitsu.iroha.android.sample.interactor;


import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.BlockOuterClass;
import iroha.protocol.CommandServiceGrpc;
import jp.co.soramitsu.iroha.android.ByteVector;
import jp.co.soramitsu.iroha.android.Keypair;
import jp.co.soramitsu.iroha.android.ModelCrypto;
import jp.co.soramitsu.iroha.android.ModelProtoTransaction;
import jp.co.soramitsu.iroha.android.ModelTransactionBuilder;
import jp.co.soramitsu.iroha.android.UnsignedTx;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CREATOR;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.PRIV_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.PUB_KEY;

public class GenerateQRInteractor extends SingleInteractor<Bitmap, String> {

    private final PreferencesUtil preferenceUtils;

    @Inject
    GenerateQRInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                         @Named(ApplicationModule.UI) Scheduler uiScheduler,
                         PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    private final int SIZE = 500;

    @Override
    protected Single<Bitmap> build(String amount) {
        return Single.create(emitter -> {
            String username = preferenceUtils.retrieveUsername();
            String qrText = username + "," + amount;
                        Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            QRCode qrCode = Encoder.encode(qrText, ErrorCorrectionLevel.H, hints);
            final ByteMatrix byteMatrix = qrCode.getMatrix();
            final int width = byteMatrix.getWidth();
            final int height = byteMatrix.getHeight();
            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte val = byteMatrix.get(x, y);
                    bitmap.setPixel(x, y, val == 1 ? Color.BLACK : Color.WHITE);
                }
            }
            emitter.onSuccess(Bitmap.createScaledBitmap(bitmap, SIZE, SIZE, false));
        });
    }
}