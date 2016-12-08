package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.model.QRType;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.AssetSenderView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.MessageDigest;
import io.soramitsu.irohaandroid.callback.Callback;
import io.soramitsu.irohaandroid.model.KeyPair;

public class AssetSenderPresenter implements Presenter<AssetSenderView> {

    private AssetSenderView assetSenderView;

    @Override
    public void setView(@NonNull AssetSenderView view) {
        assetSenderView = view;
    }

    @Override
    public void onCreate() {
        // nothing
    }

    @Override
    public void onStart() {
        // nothing
    }

    @Override
    public void onResume() {
        // nothing
    }

    @Override
    public void onPause() {
        // nothing
    }

    @Override
    public void onStop() {
        // nothing
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public View.OnClickListener onSubmitClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        };
    }

    private void send() {
        assetSenderView.showProgressDialog();

        final String assetUuid = ""; // TODO asset-uuidなるものをセットする
        final String command = QRType.TRANSFER.getType();
        final String value = assetSenderView.getAmount();
        final String sender = KeyPair.getKeyPair(assetSenderView.getContext()).publicKey;
        final String receiver = assetSenderView.getReceiver();
        final String message = ""; // TODO signatureのフォーマットが決まり次第
        final String signature = MessageDigest.digest(message, MessageDigest.Algorithm.SHA3_256);

        final Context context = assetSenderView.getContext();
        Iroha.getInstance().operationAsset(assetUuid, command, value, sender, receiver, signature,
                new Callback<Boolean>() {
                    @Override
                    public void onSuccessful(Boolean result) {
                        assetSenderView.hideProgressDialog();

                        assetSenderView.showSuccess(
                                context.getString(R.string.successful_title_sent),
                                context.getString(R.string.message_send_asset_successful,
                                        assetSenderView.getReceiver(), assetSenderView.getAmount()),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        assetSenderView.hideSuccess();
                                        assetSenderView.beforeQRReadViewState();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        assetSenderView.hideProgressDialog();

                        if (NetworkUtil.isOnline(assetSenderView.getContext())) {
                            assetSenderView.showError(context.getString(R.string.error_message_retry_again));
                        } else {
                            assetSenderView.showError(context.getString(R.string.error_message_check_network_state));
                        }
                    }
                }
        );
    }

    public View.OnClickListener onQRShowClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetSenderView.showQRReader();
            }
        };
    }

    public View.OnClickListener onResetClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetSenderView.reset();
                assetSenderView.beforeQRReadViewState();
            }
        };
    }
}
