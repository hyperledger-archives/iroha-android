package io.soramitsu.iroha.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentAssetSenderBinding;
import io.soramitsu.iroha.model.QRType;
import io.soramitsu.iroha.model.TransferQRParameter;
import io.soramitsu.iroha.presenter.AssetSenderPresenter;
import io.soramitsu.iroha.util.AndroidSupportUtil;
import io.soramitsu.iroha.view.AssetSenderView;
import io.soramitsu.iroha.view.activity.MainActivity;
import io.soramitsu.iroha.view.dialog.ErrorDialog;
import io.soramitsu.iroha.view.dialog.OnQRReaderListener;
import io.soramitsu.iroha.view.dialog.ProgressDialog;
import io.soramitsu.iroha.view.dialog.SuccessDialog;
import io.soramitsu.irohaandroid.model.Account;

public class AssetSenderFragment extends Fragment
        implements AssetSenderView, MainActivity.MainActivityListener, OnQRReaderListener {
    public static final String TAG = AssetSenderFragment.class.getSimpleName();

    private AssetSenderPresenter assetSenderPresenter = new AssetSenderPresenter();

    private FragmentAssetSenderBinding binding;
    private ErrorDialog errorDialog;
    private SuccessDialog successDialog;
    private ProgressDialog progressDialog;

    public static AssetSenderFragment newInstance() {
        AssetSenderFragment fragment = new AssetSenderFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetSenderPresenter.setView(this);
        assetSenderPresenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        errorDialog = new ErrorDialog(inflater);
        successDialog = new SuccessDialog(inflater);
        progressDialog = new ProgressDialog(inflater);
        return inflater.inflate(R.layout.fragment_asset_sender, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        View.OnClickListener onQRShowClickListener = assetSenderPresenter.onQRShowClicked();
        binding.qrImage.setOnClickListener(onQRShowClickListener);
        binding.qrLabel.setOnClickListener(onQRShowClickListener);
        binding.resetButton.setOnClickListener(assetSenderPresenter.onResetClicked());
        binding.submitButton.setOnClickListener(assetSenderPresenter.onSubmitClicked());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (binding.accountNum.getText().length() != 0) {
            Log.d(TAG, "onStart: " + binding.hiddenReceiverPublicKey.getText().toString());
            afterQRReadViewState(
                    binding.accountNum.getText().toString(),
                    binding.hiddenReceiverPublicKey.getText().toString(),
                    binding.remittanceAmount.getText().toString()
            );
        } else {
            beforeQRReadViewState();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        assetSenderPresenter.onStop();
    }

    @Override
    public void showError(String error) {
        errorDialog.show(getActivity(), error);
    }

    @Override
    public void showSuccess(String title, String message, View.OnClickListener onClickListener) {
        successDialog.show(getActivity(), title, message, onClickListener);
    }

    @Override
    public void hideSuccess() {
        successDialog.hide();
    }

    @Override
    public String getAmount() {
        return binding.remittanceAmount.getText().toString();
    }

    @Override
    public String getReceiver() {
        Log.d(TAG, "getReceiver: " + binding.hiddenReceiverPublicKey.getText().toString());
        return binding.hiddenReceiverPublicKey.getText().toString();
    }

    @Override
    public void showQRReader() {
        AndroidSupportUtil.showQRReaderDialog(getChildFragmentManager());
    }

    @Override
    public void reset() {
        binding.accountNum.setText("");
        binding.remittanceAmount.setText("");
    }

    @Override
    public void beforeQRReadViewState() {
        binding.hiddenReceiverPublicKey.setText("");
        binding.accountNum.setText("");
        binding.remittanceAmount.setText("");
        binding.getRoot().setBackgroundColor(AndroidSupportUtil.getColor(getContext(), R.color.colorPrimary));
        binding.qrContainer.setVisibility(View.VISIBLE);
        binding.accountNumContainerWrapper.setVisibility(View.GONE);
        binding.buttonContainer.setVisibility(View.GONE);
    }

    @Override
    public void afterQRReadViewState(String alias, String receiver, String value) {
        binding.hiddenReceiverPublicKey.setText(receiver);
        binding.accountNum.setText(alias);
        binding.remittanceAmount.setText(value);
        binding.getRoot().setBackground(null);
        binding.qrContainer.setVisibility(View.GONE);
        binding.accountNumContainerWrapper.setVisibility(View.VISIBLE);
        binding.buttonContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show(getActivity(), getString(R.string.sending));
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.hide();
    }

    @Override
    public void onNavigationItemClicked() {
        // nothing
    }

    @Override
    public void setOnResult(String result) {
        TransferQRParameter params;
        try {
            params = new Gson().fromJson(result, TransferQRParameter.class);
        } catch (Exception e) {
            Log.e(TAG, "setOnResult: json could not parse to object!");
            errorDialog.show(getActivity(), getString(R.string.error_message_illegal_qr));
            return;
        }

        if (params == null || !params.type.equals(QRType.TRANSFER.getType())) {
            Log.e(TAG, "setOnResult: QR type is not transfer!");
            errorDialog.show(getActivity(), getString(R.string.error_message_illegal_qr));
            return;
        }

        if (params.value <= 0) {
            Log.e(TAG, "setOnResult: QR value is lower than 0!");
            errorDialog.show(getActivity(), getString(R.string.error_message_request_amount_is_incorrect));
            return;
        }

        if (params.account.equals(Account.getUuid(getContext()))) {
            Log.e(TAG, "setOnResult: This QR is mine!");
            errorDialog.show(getActivity(), getString(R.string.error_message_cannot_send_to_myself));
            return;
        }

        afterQRReadViewState(
                params.alias == null ? "不明な送信先" : params.alias,
                params.account,
                String.valueOf(params.value)
        );
    }
}
