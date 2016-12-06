package io.soramitsu.iroha.view.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentAssetReceiverBinding;
import io.soramitsu.iroha.presenter.AssetReceiverPresenter;
import io.soramitsu.iroha.view.AssetReceiverView;
import io.soramitsu.iroha.view.dialog.ErrorDialog;

public class AssetReceiverFragment extends Fragment implements AssetReceiverView {
    public static final String TAG = AssetReceiverFragment.class.getSimpleName();

    private AssetReceiverPresenter assetReceiverPresenter = new AssetReceiverPresenter();

    private FragmentAssetReceiverBinding binding;
    private ErrorDialog errorDialog;

    public static AssetReceiverFragment newInstance() {
        AssetReceiverFragment fragment = new AssetReceiverFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetReceiverPresenter.setView(this);
        assetReceiverPresenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        errorDialog = new ErrorDialog(inflater);
        return inflater.inflate(R.layout.fragment_asset_receiver, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.receiverAmount.addTextChangedListener(assetReceiverPresenter.textWatcher());
        binding.calculatorKeyboard.setTargetTextView(binding.receiverAmount);
        assetReceiverPresenter.defaultQR();
    }

    @Override
    public void showError(String error) {
        errorDialog.show(getActivity(), error);
    }

    @Override
    public String getAmount() {
        return binding.receiverAmount.getText().toString();
    }

    @Override
    public void setAmount(String amount) {
        binding.receiverAmount.setText(amount);
    }

    @Override
    public void invalidate() {
        binding.imageViewQrCode.invalidate();
    }

    @Override
    public void setQR(Bitmap qr) {
        binding.imageViewQrCode.setImageBitmap(qr);
    }
}
