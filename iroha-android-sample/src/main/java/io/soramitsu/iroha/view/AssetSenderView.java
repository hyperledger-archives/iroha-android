package io.soramitsu.iroha.view;

import android.view.View;

public interface AssetSenderView extends LoadingView {
    void showError(String error);

    void showSuccess(String title, String message, View.OnClickListener onClickListener);

    void hideSuccess();

    String getAmount();

    void setAmount(String amount);

    String getReceiver();

    void showQRReader();

    void beforeQRReadViewState();

    void afterQRReadViewState(String receiver, String value);
}
