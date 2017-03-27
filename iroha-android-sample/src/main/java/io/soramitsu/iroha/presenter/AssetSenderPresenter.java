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

package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

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
import io.soramitsu.irohaandroid.model.KeyPair;
import io.soramitsu.irohaandroid.security.KeyGenerator;
import io.soramitsu.irohaandroid.security.MessageDigest;

public class AssetSenderPresenter implements Presenter<AssetSenderView> {
    public static final String TAG = AssetSenderPresenter.class.getSimpleName();

    public static final String IROHA_TASK_TAG_SEND = "AssetSend";

    private AssetSenderView assetSenderView;

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
        keyPair = getKeyPair();
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
        Iroha.getInstance().cancelAsyncTask(IROHA_TASK_TAG_SEND);
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
                } catch (ReceiverNotFoundException | SelfSendCanNotException e) {
                    assetSenderView.showWarning(ErrorMessageFactory.create(assetSenderView.getContext(), e));
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

    public TextWatcher textWatcher() {
        return new TextWatcher() {
            private boolean isAmountEmpty;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String amount = assetSenderView.getAmount();
                isAmountEmpty = amount == null || amount.isEmpty();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isAmountEmpty && charSequence.toString().equals("0")) {
                    assetSenderView.setAmount("");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // nothing
            }
        };
    }

    private void send() throws ReceiverNotFoundException, SelfSendCanNotException {
        final String receiver = assetSenderView.getReceiver();
        final String amount = assetSenderView.getAmount();

        if (receiver.isEmpty() || amount.isEmpty()) {
            throw new ReceiverNotFoundException();
        } else if (isQRMine()) {
            throw new SelfSendCanNotException();
        } else {
            assetSenderView.showProgress();

            final String assetUuid = "60f4a396b520d6c54e33634d060751814e0c4bf103a81c58da704bba82461c32";
            final String command = QRType.TRANSFER.getType().toLowerCase();
            final String sender = keyPair.publicKey;
            final long timestamp = System.currentTimeMillis() / 1000;
            final String message = generateMessage(timestamp, amount, sender, receiver, command, assetUuid);
            final String signature = Iroha.sign(keyPair, MessageDigest.digest(message, MessageDigest.Algorithm.SHA3_256));

            Iroha iroha = Iroha.getInstance();
            iroha.runAsyncTask(
                    IROHA_TASK_TAG_SEND,
                    iroha.operateAssetFunction(
                            assetUuid,
                            command,
                            amount,
                            sender,
                            receiver,
                            signature,
                            timestamp
                    ),
                    callback()
            );
        }
    }

    private Callback<Boolean> callback() {
        final Context c = assetSenderView.getContext();
        return new Callback<Boolean>() {
            @Override
            public void onSuccessful(Boolean result) {
                assetSenderView.hideProgress();

                assetSenderView.showSuccess(
                        c.getString(R.string.successful_title_sent),
                        c.getString(R.string.message_send_asset_successful,
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
                    assetSenderView.showError(ErrorMessageFactory.create(c, throwable));
                } else {
                    assetSenderView.showWarning(ErrorMessageFactory.create(c, new NetworkNotConnectedException()));
                }
            }
        };
    }

    private String generateMessage(long timestamp, String value, String sender,
                                   String receiver, String command, String uuid) {
        return "timestamp:" + timestamp
                + ",value:" + value
                + ",sender:" + sender
                + ",receiver:" + receiver
                + ",command:" + command
                + ",asset-uuid:" + uuid;
    }

    @NotNull
    private KeyPair getKeyPair() {
        if (keyPair == null) {
            final Context context = assetSenderView.getContext();
            keyPair = KeyPair.getKeyPair(context);
        }
        return keyPair;
    }

    private boolean isQRMine() throws SelfSendCanNotException {
        return assetSenderView.getReceiver().equals(keyPair.publicKey);
    }
}
