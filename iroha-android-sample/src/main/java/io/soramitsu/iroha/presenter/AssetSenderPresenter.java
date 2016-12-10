package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.IlligalQRCodeException;
import io.soramitsu.iroha.exception.IlligalRequestAmountException;
import io.soramitsu.iroha.exception.NetworkNotConnectedException;
import io.soramitsu.iroha.exception.SelfSendCanNotException;
import io.soramitsu.iroha.model.QRType;
import io.soramitsu.iroha.model.TransferQRParameter;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.AssetSenderView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.MessageDigest;
import io.soramitsu.irohaandroid.callback.Callback;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.model.KeyPair;

public class AssetSenderPresenter implements Presenter<AssetSenderView> {
    public static final String TAG = AssetSenderPresenter.class.getSimpleName();

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
                                        assetSenderView.getReceiverAlias(), assetSenderView.getAmount()),
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
                            assetSenderView.showError(ErrorMessageFactory.create(context, throwable));
                        } else {
                            assetSenderView.showError(ErrorMessageFactory.create(context, new NetworkNotConnectedException()));
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

    public Callback<String> onReadQR() {
        return new Callback<String>() {
            @Override
            public void onSuccessful(String result) {
                Log.d(TAG, "onSuccessful: " + result);

                final Context context = assetSenderView.getContext();

                TransferQRParameter params;
                try {
                    params = new Gson().fromJson(result, TransferQRParameter.class);
                } catch (Exception e) {
                    Log.e(TAG, "setOnResult: json could not parse to object!");
                    assetSenderView.showError(ErrorMessageFactory.create(context, new IlligalQRCodeException()));
                    return;
                }

                if (params == null || !params.type.equals(QRType.TRANSFER.getType())) {
                    Log.e(TAG, "setOnResult: QR type is not transfer!");
                    assetSenderView.showError(ErrorMessageFactory.create(context, new IlligalQRCodeException()));
                    return;
                }

                if (params.value <= 0) {
                    Log.e(TAG, "setOnResult: QR value is lower than 0!");
                    assetSenderView.showError(ErrorMessageFactory.create(context, new IlligalRequestAmountException()));
                    return;
                }

                if (params.account.equals(Account.getUuid(context))) {
                    Log.e(TAG, "setOnResult: This QR is mine!");
                    assetSenderView.showError(ErrorMessageFactory.create(context, new SelfSendCanNotException()));
                    return;
                }

                assetSenderView.afterQRReadViewState(
                        params.alias == null ? context.getString(R.string.unknown_receiver) : params.alias,
                        params.account,
                        String.valueOf(params.value)
                );
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "onFailure: ", throwable);
            }
        };
    }
}
