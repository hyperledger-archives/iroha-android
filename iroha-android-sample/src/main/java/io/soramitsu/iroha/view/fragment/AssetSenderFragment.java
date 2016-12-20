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

package io.soramitsu.iroha.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentAssetSenderBinding;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.presenter.AssetSenderPresenter;
import io.soramitsu.iroha.view.AssetSenderView;
import io.soramitsu.iroha.view.activity.MainActivity;
import io.soramitsu.iroha.view.dialog.ErrorDialog;
import io.soramitsu.iroha.view.dialog.ProgressDialog;
import io.soramitsu.iroha.view.dialog.SuccessDialog;

public class AssetSenderFragment extends Fragment
        implements AssetSenderView, MainActivity.MainActivityListener {
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
        binding.qrButton.setOnClickListener(assetSenderPresenter.onQRShowClicked());
        binding.submitButton.setOnClickListener(assetSenderPresenter.onSubmitClicked());
        binding.amount.addTextChangedListener(assetSenderPresenter.textWatcher());
    }

    @Override
    public void onStart() {
        super.onStart();
        assetSenderPresenter.onStart();
        if (binding.receiver.getText().length() != 0) {
            Log.d(TAG, "onStart: " + binding.receiver.getText().toString());
            afterQRReadViewState(
                    binding.receiver.getText().toString(),
                    binding.amount.getText().toString()
            );
        } else {
            beforeQRReadViewState();
        }
    }

    @Override
    public void onStop() {
        assetSenderPresenter.onStop();
        super.onStop();
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
        return binding.amount.getText().toString();
    }

    @Override
    public void setAmount(String amount) {
        binding.amount.setText(amount);
    }

    @Override
    public String getReceiver() {
        Log.d(TAG, "getReceiver: " + binding.receiver.getText().toString());
        return binding.receiver.getText().toString();
    }

    @Override
    public void showQRReader() {
        Navigator.getInstance().navigateToQRReaderActivity(getContext(), assetSenderPresenter.onReadQR());
    }

    @Override
    public void beforeQRReadViewState() {
        binding.receiver.setText("");
        binding.amount.setText("");
    }

    @Override
    public void afterQRReadViewState(String receiver, String value) {
        binding.receiver.setText(receiver);
        binding.amount.setText(value);
    }

    @Override
    public void showProgress() {
        progressDialog.show(getActivity(), getString(R.string.sending));
    }

    @Override
    public void hideProgress() {
        progressDialog.hide();
    }

    @Override
    public void onNavigationItemClicked() {
        // nothing
    }
}
