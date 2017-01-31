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

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.WriterException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.crypto.NoSuchPaddingException;

import click.kobaken.rxirohaandroid.Iroha;
import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.KeyPair;
import click.kobaken.rxirohaandroid.qr.QRCodeGenerator;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.soramitsu.iroha.R;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.model.TransferQRParameter;
import io.soramitsu.iroha.view.AssetReceiveView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AssetReceivePresenter implements Presenter<AssetReceiveView> {
    public static final String TAG = AssetReceivePresenter.class.getSimpleName();

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private AssetReceiveView assetReceiveView;
    private CompositeDisposable compositeDisposable;

    private Handler refreshHandler;
    private Runnable transactionRunnable;

    private String uuid;
    private String publicKey;
    private Bitmap qr;

    @Override
    public void setView(@NonNull AssetReceiveView view) {
        assetReceiveView = view;
    }

    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onStart() {
        refreshHandler = new Handler();
        transactionRunnable = new Runnable() {
            @Override
            public void run() {
                fetchAccountAssetFromApi();
            }
        };

        generateQR();
        fetchAccountAsset();
        setPublicKey();
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SwipeRefreshLayout.OnRefreshListener onSwipeRefresh() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHandler.postDelayed(transactionRunnable, 1500);
            }
        };
    }

    public View.OnClickListener onPublicKeyTextClicked() {
        return new View.OnClickListener() {
            private static final String CLIP_DATA_LABEL_TEXT_DATA = "text_data";

            @Override
            public void onClick(View view) {
                final Context context = assetReceiveView.getContext();

                ClipData.Item item = new ClipData.Item(assetReceiveView.getPublicKey());

                String[] mimeType = new String[1];
                mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;

                ClipData cd = new ClipData(new ClipDescription(CLIP_DATA_LABEL_TEXT_DATA, mimeType), item);

                ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(cd);

                Toast.makeText(context, R.string.message_copy_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public TextWatcher textWatcher() {
        return new TextWatcher() {
            private boolean isAmountEmpty;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String amount = assetReceiveView.getAmount();
                isAmountEmpty = amount == null || amount.isEmpty();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isAmountEmpty && charSequence.toString().equals("0")) {
                    assetReceiveView.setAmount("");
                    return;
                }
                changeQR();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // nothing
            }
        };
    }

    private void fetchAccountAsset() {
        String assetValue = assetReceiveView.getHasAssetValue();
        if (assetValue == null) {
            fetchAccountAssetFromApi();
        } else {
            assetReceiveView.setHasAssetValue(assetValue);
        }
    }

    private void fetchAccountAssetFromApi() {
        if (uuid == null || uuid.isEmpty()) {
            uuid = getUuid();
        }

        Disposable disposable = Iroha.getInstance()
                .findAccount(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account result) {
                        if (assetReceiveView.isRefreshing()) {
                            assetReceiveView.setRefreshing(false);
                        }

                        if (result != null && result.assets != null && !result.assets.isEmpty()) {
                            assetReceiveView.setHasAssetValue(result.assets.get(0).value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (assetReceiveView.isRefreshing()) {
                            assetReceiveView.setRefreshing(false);
                        }

                        assetReceiveView.showError(
                                ErrorMessageFactory.create(assetReceiveView.getContext(), e)
                        );
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void setPublicKey() {
        assetReceiveView.setPublicKey(getPublicKey());
    }

    @NotNull
    private String getPublicKey() {
        if (publicKey == null || publicKey.isEmpty()) {
            final Context context = assetReceiveView.getContext();
            try {
                publicKey = KeyPair.getKeyPair(context).publicKey;
            } catch (NoSuchPaddingException | UnrecoverableKeyException | NoSuchAlgorithmException
                    | KeyStoreException | InvalidKeyException | IOException e) {
                Log.e(TAG, "getKeyPair: ", e);
                assetReceiveView.showError(ErrorMessageFactory.create(context, e));
                return "";
            }
        }
        return publicKey;
    }

    @NotNull
    private String getUuid() {
        final Context context = assetReceiveView.getContext();
        if (uuid == null || uuid.isEmpty()) {
            try {
                uuid = Account.getUuid(context);
            } catch (NoSuchPaddingException | UnrecoverableKeyException | NoSuchAlgorithmException
                    | KeyStoreException | InvalidKeyException | IOException e) {
                assetReceiveView.showError(ErrorMessageFactory.create(context, e));
                return "";
            }
        }
        return uuid;
    }

    private void generateQR() {
        try {
            if (qr == null) {
                Log.d(TAG, "generateQR: new generation!");
                qr = QRCodeGenerator.generateQR(generateQrParamsText(), 500, QRCodeGenerator.ENCODE_CHARACTER_TYPE_UTF_8);
            }
            setQR(qr);
        } catch (WriterException e) {
            assetReceiveView.showError(ErrorMessageFactory.create(assetReceiveView.getContext(), e));
        }
    }

    private void changeQR() {
        try {
            qr = QRCodeGenerator.generateQR(generateQrParamsText(), 500, QRCodeGenerator.ENCODE_CHARACTER_TYPE_UTF_8);
            setQR(qr);
        } catch (WriterException e) {
            assetReceiveView.showError(ErrorMessageFactory.create(assetReceiveView.getContext(), e));
        }
    }

    private void setQR(Bitmap qr) {
        assetReceiveView.setQR(qr);
        assetReceiveView.invalidate();
    }

    private String generateQrParamsText() {
        final TransferQRParameter qrParams = new TransferQRParameter();
        qrParams.account = getPublicKey();
        qrParams.amount = getValueForReceiveAmount();

        return gson.toJson(qrParams, TransferQRParameter.class);
    }

    private int getValueForReceiveAmount() {
        int value;
        try {
            value = Integer.parseInt(assetReceiveView.getAmount());
        } catch (NumberFormatException e) {
            value = 0;
        }
        return value;
    }
}
