package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.crypto.NoSuchPaddingException;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.IllegalQRCodeException;
import io.soramitsu.iroha.exception.NetworkNotConnectedException;
import io.soramitsu.iroha.exception.ReceiverNotFoundException;
import io.soramitsu.iroha.exception.SelfSendCanNotException;
import io.soramitsu.iroha.model.QRType;
import io.soramitsu.iroha.model.TransferQRParameter;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.AssetSenderView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.callback.Callback;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.model.KeyPair;

public class AssetSenderPresenter implements Presenter<AssetSenderView> {
    public static final String TAG = AssetSenderPresenter.class.getSimpleName();

    private AssetSenderView assetSenderView;

    private String uuid;
    private KeyPair keyPair;

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
        Iroha.getInstance().cancelOperationAsset();
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public View.OnClickListener onSubmitClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    send();
                } catch (ReceiverNotFoundException e) {
                    assetSenderView.showError(ErrorMessageFactory.create(assetSenderView.getContext(), e));
                }
            }
        };
    }

    public View.OnClickListener onQRShowClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetSenderView.showQRReader();
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
                    assetSenderView.showError(ErrorMessageFactory.create(context, new IllegalQRCodeException()));
                    return;
                }

                if (uuid == null || uuid.isEmpty()) {
                    uuid = getUuid();
                }

                if (params.account.equals(uuid)) {
                    Log.e(TAG, "setOnResult: This QR is mine!");
                    assetSenderView.showError(ErrorMessageFactory.create(context, new SelfSendCanNotException()));
                    return;
                }

                final String value = String.valueOf(params.amount).equals("0")
                        ? ""
                        : String.valueOf(params.amount);
                assetSenderView.afterQRReadViewState(params.account, value);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "onFailure: ", throwable);
            }
        };
    }

    private void send() throws ReceiverNotFoundException {
        if (assetSenderView.getReceiver().isEmpty() || assetSenderView.getAmount().isEmpty()) {
            throw new ReceiverNotFoundException();
        }

        assetSenderView.showProgress();

        final String assetUuid = "60f4a396b520d6c54e33634d060751814e0c4bf103a81c58da704bba82461c32";
        final String command = QRType.TRANSFER.getType();
        final String value = assetSenderView.getAmount();
        final String receiver = assetSenderView.getReceiver();
        final String sender = getKeyPair().publicKey;

        final Context context = assetSenderView.getContext();
        Iroha.getInstance().operationAsset(assetUuid, command, value, sender, receiver,
                new Callback<Boolean>() {
                    @Override
                    public void onSuccessful(Boolean result) {
                        assetSenderView.hideProgress();

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
                        assetSenderView.hideProgress();

                        if (NetworkUtil.isOnline(assetSenderView.getContext())) {
                            assetSenderView.showError(ErrorMessageFactory.create(context, throwable));
                        } else {
                            assetSenderView.showError(ErrorMessageFactory.create(context, new NetworkNotConnectedException()));
                        }
                    }
                }
        );
    }

    private KeyPair getKeyPair() {
        if (keyPair == null) {
            final Context context = assetSenderView.getContext();
            try {
                keyPair = KeyPair.getKeyPair(context);
            } catch (NoSuchPaddingException | UnrecoverableKeyException | NoSuchAlgorithmException
                    | KeyStoreException | InvalidKeyException | IOException e) {
                Log.e(TAG, "getKeyPair: ", e);
                assetSenderView.showError(ErrorMessageFactory.create(context, e));
                return null;
            }
        }
        return keyPair;
    }

    private String getUuid() {
        final Context context = assetSenderView.getContext();
        final String uuid;
        try {
            uuid = Account.getUuid(context);
        } catch (NoSuchPaddingException | UnrecoverableKeyException | NoSuchAlgorithmException
                | KeyStoreException | InvalidKeyException | IOException e) {
            assetSenderView.showError(ErrorMessageFactory.create(context, e));
            return null;
        }
        return uuid;
    }
}
