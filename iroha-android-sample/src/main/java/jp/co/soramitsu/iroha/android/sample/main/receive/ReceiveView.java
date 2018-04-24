package jp.co.soramitsu.iroha.android.sample.main.receive;

import android.graphics.Bitmap;

public interface ReceiveView {

    void didGenerateSuccess(Bitmap bitmap);

    void resetQR();

    void didError(Throwable error);

}
