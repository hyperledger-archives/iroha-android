package io.soramitsu.iroha.view;

import android.graphics.Bitmap;

public interface AssetReceiveView extends View {
    boolean isRefreshing();

    void setRefreshing(boolean refreshing);

    void showError(String error);

    String getAmount();

    void setAmount(String amount);

    void setPublicKey(String publicKey);

    void invalidate();

    void setQR(Bitmap qr);

    String getHasAssetValue();

    void setHasAssetValue(String value);
}
