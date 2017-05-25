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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.soramitsu.iroha.R;
import io.soramitsu.iroha.api.IrohaClient;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.NetworkNotConnectedException;
import io.soramitsu.iroha.exception.ReceiverNotFoundException;
import io.soramitsu.iroha.exception.SelfSendCanNotException;
import io.soramitsu.iroha.model.QRType;
import io.soramitsu.iroha.util.DateUtil;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.AssetSenderView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.KeyPair;
import io.soramitsu.irohaandroid.security.MessageDigest;

public class AssetSenderPresenter implements Presenter<AssetSenderView> {
    public static final String TAG = AssetSenderPresenter.class.getSimpleName();

    private static final String IROHA_ASSET_UUID = "60f4a396b520d6c54e33634d060751814e0c4bf103a81c58da704bba82461c32";

    private AssetSenderView assetSenderView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        // nothing
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
    }

    public View.OnClickListener onSubmitClicked() {
        return v -> {
            try {
                send();
            } catch (ReceiverNotFoundException | SelfSendCanNotException e) {
                assetSenderView.showWarning(ErrorMessageFactory.create(assetSenderView.getContext(), e));
            }
        };
    }

    public View.OnClickListener onQRShowClicked() {
        return v -> assetSenderView.showQRReader();
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

        if (TextUtils.isEmpty(receiver) || TextUtils.isEmpty(amount)) {
            throw new ReceiverNotFoundException();
        } else if (isQRMine()) {
            throw new SelfSendCanNotException();
        } else {
            assetSenderView.showProgress();

            final String command = QRType.TRANSFER.getType().toLowerCase();
            final String sender = keyPair.publicKey;
            final long timestamp = DateUtil.currentUnixTimestamp();
            final String signature = Iroha.sign(keyPair, MessageDigest.digest(
                    generateMessage(timestamp, amount, sender, receiver, command, IROHA_ASSET_UUID),
                    MessageDigest.Algorithm.SHA3_256)
            );

            Disposable disposable = IrohaClient.getInstance()
                    .operation(sender, command, sender, receiver, amount, signature, timestamp)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSuccess, this::onError);
            compositeDisposable.add(disposable);
        }
    }

    private void onSuccess() {
        assetSenderView.hideProgress();

        final Context ctx = assetSenderView.getContext();

        assetSenderView.showSuccess(
                ctx.getString(R.string.successful_title_sent),
                ctx.getString(R.string.message_send_asset_successful,
                        assetSenderView.getReceiver(), assetSenderView.getAmount()),
                v -> {
                    assetSenderView.hideSuccess();
                    assetSenderView.beforeQRReadViewState();
                });
    }

    private void onError(Throwable e) {
        Log.e(TAG, "onError: ", e);

        assetSenderView.hideProgress();

        final Context ctx = assetSenderView.getContext();

        if (NetworkUtil.isOnline(assetSenderView.getContext())) {
            final String errorMessage = ErrorMessageFactory.create(ctx, e);
            assetSenderView.showError(errorMessage);
        } else {
            final String warningMessage = ErrorMessageFactory
                    .create(ctx, new NetworkNotConnectedException());
            assetSenderView.showWarning(warningMessage);
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

    @NonNull
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
