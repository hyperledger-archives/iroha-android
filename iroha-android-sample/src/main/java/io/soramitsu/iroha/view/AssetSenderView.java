package io.soramitsu.iroha.view;

import android.view.View;

public interface AssetSenderView extends LoadingView {
    void showError(String error);
    void showSuccess(String title, String message, View.OnClickListener onClickListener);
    void hideSuccess();
    String getAmount();
    String getReceiver();
    String getReceiverAlias();
    void showQRReader();
    void reset();
    void beforeQRReadViewState();
    void afterQRReadViewState(String alias, String receiver, String value);
}
