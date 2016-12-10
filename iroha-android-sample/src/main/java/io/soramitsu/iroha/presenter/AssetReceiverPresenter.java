package io.soramitsu.iroha.presenter;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.zxing.WriterException;

import java.io.UnsupportedEncodingException;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.LargeNumberOfDigitsException;
import io.soramitsu.iroha.model.QRType;
import io.soramitsu.iroha.model.TransferQRParameter;
import io.soramitsu.iroha.view.AssetReceiverView;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.model.KeyPair;
import io.soramitsu.irohaandroid.qr.QRCodeGenerator;

import static io.soramitsu.iroha.model.TransferQRParameter.QR_TEXT_DEFAULT;

public class AssetReceiverPresenter implements Presenter<AssetReceiverView> {
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
        // nothing
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public void defaultQR() {
        generateQR(QR_TEXT_DEFAULT);
    }

    private void generateQR(String qrParamsText) {
        try {
            assetReceiverView.setQR(QRCodeGenerator.generateQR(qrParamsText, 500, QRCodeGenerator.ENCODE_CHARACTER_TYPE_UTF_8));
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
                    assetReceiverView.showError(
                            ErrorMessageFactory.create(
                                    assetReceiverView.getContext(),
                                    new LargeNumberOfDigitsException()
                            )
                    );
                    return;
                }

                final TransferQRParameter qrParams = new TransferQRParameter();
                qrParams.type = QRType.TRANSFER.getType();
                qrParams.account = KeyPair.getKeyPair(assetReceiverView.getContext()).publicKey;
                qrParams.value = value;

                try {
                    qrParams.alias = new String(Account.getAlias(assetReceiverView.getContext()).getBytes("UTF-8"), "UTF-8");
                    String qrParamsText = new GsonBuilder()
                            .disableHtmlEscaping()
                            .create()
                            .toJson(qrParams, TransferQRParameter.class);
                    Log.d(TAG, "onTextChanged: " + qrParamsText);
                    generateQR(qrParamsText);
                    assetReceiverView.invalidate();
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "onSuccessful: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // nothing
            }
        };
    }
}
