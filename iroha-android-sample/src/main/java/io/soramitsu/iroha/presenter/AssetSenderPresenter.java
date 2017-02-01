/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.crypto.NoSuchPaddingException;

import click.kobaken.rxirohaandroid.Iroha;
import click.kobaken.rxirohaandroid.model.BaseModel;
import click.kobaken.rxirohaandroid.model.KeyPair;
import click.kobaken.rxirohaandroid.qr.ReadQRCallback;
import click.kobaken.rxirohaandroid.security.MessageDigest;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
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

public class AssetSenderPresenter implements Presenter<AssetSenderView> {
    public static final String TAG = AssetSenderPresenter.class.getSimpleName();

    private AssetSenderView assetSenderView;
    private CompositeDisposable compositeDisposable;

    private KeyPair keyPair;

    @Override
    public void setView(@NonNull AssetSenderView view) {
        assetSenderView = view;
    }

    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable();
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
        // nothing
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
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

    public ReadQRCallback onReadQR() {
        return new ReadQRCallback() {
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

    private void send() throws ReceiverNotFoundException {
        final String receiver = assetSenderView.getReceiver();
        final String amount = assetSenderView.getAmount();

        if (receiver.isEmpty() || amount.isEmpty()) {
            throw new ReceiverNotFoundException();
        }

        assetSenderView.showProgress();

        if (validation()) {
            final String assetUuid = "60f4a396b520d6c54e33634d060751814e0c4bf103a81c58da704bba82461c32";
            final String command = QRType.TRANSFER.getType();
            final String sender = keyPair.publicKey;
            final long timestamp = System.currentTimeMillis() / 1000;
            final String message = generateMessage(timestamp, amount, sender, receiver, command, assetUuid);
            final String signature = MessageDigest.digest(message, MessageDigest.Algorithm.SHA3_256);

            assetSenderView.hideProgress();

            Disposable disposable = Iroha.getInstance()
                    .operateAsset(
                            assetUuid,
                            command,
                            amount,
                            sender,
                            receiver,
                            signature,
                            timestamp
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<BaseModel>() {
                        Context c = assetSenderView.getContext();

                        @Override
                        public void onNext(BaseModel result) {
                            Log.d(TAG, "onNext: " + result.message);
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
                        public void onError(Throwable e) {
                            assetSenderView.hideProgress();

                            if (NetworkUtil.isOnline(assetSenderView.getContext())) {
                                assetSenderView.showError(ErrorMessageFactory.create(c, e));
                            } else {
                                assetSenderView.showError(ErrorMessageFactory.create(c, new NetworkNotConnectedException()));
                            }
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete: ");
                        }
                    });

            compositeDisposable.add(disposable);
        } else {
            assetSenderView.hideProgress();
        }
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
            try {
                keyPair = KeyPair.getKeyPair(context);
            } catch (NoSuchPaddingException | UnrecoverableKeyException | NoSuchAlgorithmException
                    | KeyStoreException | InvalidKeyException | IOException e) {
                Log.e(TAG, "getKeyPair: ", e);
                assetSenderView.showError(ErrorMessageFactory.create(context, e));
                return new KeyPair("", "");
            }
        }
        return keyPair;
    }

    private boolean validation() {
        if (assetSenderView.getReceiver().equals(keyPair.publicKey)) {
            Log.e(TAG, "setOnResult: This QR is mine!");
            assetSenderView.showError(
                    ErrorMessageFactory.create(
                            assetSenderView.getContext(),
                            new SelfSendCanNotException()
                    )
            );
            return false;
        }
        return true;
    }
}
