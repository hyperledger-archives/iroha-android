package io.soramitsu.iroha.view;

import android.graphics.Bitmap;

public interface AssetReceiverView extends View {
    void showError(String error);
    String getAmount();
    void setAmount(String amount);
    void invalidate();
    void setQR(Bitmap qr);
}
