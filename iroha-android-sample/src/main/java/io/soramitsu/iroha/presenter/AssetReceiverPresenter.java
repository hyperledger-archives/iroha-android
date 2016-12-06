package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.zxing.WriterException;

import java.io.UnsupportedEncodingException;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.model.TransferQRParameter;
import io.soramitsu.iroha.util.QRCodeGenerator;
import io.soramitsu.iroha.view.AssetReceiverView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.domain.entity.Account;
import rx.Subscriber;

public class AssetReceiverPresenter implements Presenter<AssetReceiverView> {
    private static final String QR_TEXT_DEFAULT = "{\"type\":\"trans\",\"account\":\"\",\"value\":0}";

    public static final String TAG = AssetReceiverPresenter.class.getSimpleName();

    private AssetReceiverView assetReceiverView;

    @Override
    public void setView(@NonNull AssetReceiverView view) {
        assetReceiverView = view;
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
        Iroha.getInstance().unsbscribeFindUuid();
        Iroha.getInstance().unsubscribeFetchKeyPair();
        Iroha.getInstance().unsubscribeFindAccount();
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public void defaultQR() {
        try {
            assetReceiverView.setQR(QRCodeGenerator.generateQR(QR_TEXT_DEFAULT, 500));
        } catch (WriterException e) {
            assetReceiverView.showError(assetReceiverView.getContext().getString(R.string.error_message_cannot_generate_qr));
        }
    }

    private void generateQR(String qrParamsText) {
        try {
            assetReceiverView.setQR(QRCodeGenerator.generateQR(qrParamsText, 500));
        } catch (WriterException e) {
            assetReceiverView.showError(assetReceiverView.getContext().getString(R.string.error_message_cannot_generate_qr));
        }
    }

    public TextWatcher textWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int value;
                try {
                    value = Integer.parseInt(assetReceiverView.getAmount());
                } catch (NumberFormatException e) {
                    String buff = assetReceiverView.getAmount();
                    if (buff.length() <= 0) {
                        defaultQR();
                        return;
                    }
                    assetReceiverView.setAmount(buff.substring(0, buff.length() - 1));
                    assetReceiverView.showError("桁数が多すぎます");
                    return;
                }

                Iroha iroha = Iroha.getInstance();
                final Context context = assetReceiverView.getContext();
                final TransferQRParameter qrParams = new TransferQRParameter();
                qrParams.type = "trans";
                qrParams.account = iroha.findKeyPair(context).getPublicKey();
                qrParams.value = value;

                iroha.findAccountInfo(context, iroha.findUuid(context), new Subscriber<Account>() {
                    private String qrParamsText;

                    @Override
                    public void onCompleted() {
                        generateQR(qrParamsText);
                        assetReceiverView.invalidate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        assetReceiverView.showError(context.getString(R.string.error_message_retry_again));
                    }

                    @Override
                    public void onNext(Account account) {
                        try {
                            qrParams.alias = new String(account.name.getBytes("UTF-8"), "UTF-8");
                            qrParamsText = new GsonBuilder()
                                    .disableHtmlEscaping()
                                    .create()
                                    .toJson(qrParams, TransferQRParameter.class);
                            Log.d(TAG, "onTextChanged: " + qrParamsText);
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "onNext: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // nothing
            }
        };
    }
}
