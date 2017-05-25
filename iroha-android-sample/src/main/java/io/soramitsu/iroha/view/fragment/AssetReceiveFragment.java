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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentAssetReceiveBinding;
import io.soramitsu.iroha.presenter.AssetReceivePresenter;
import io.soramitsu.iroha.view.AssetReceiveView;
import io.soramitsu.iroha.view.activity.MainActivity;

public class AssetReceiveFragment extends Fragment
        implements AssetReceiveView, MainActivity.MainActivityListener {
    public static final String TAG = AssetReceiveFragment.class.getSimpleName();

    private static final String ARG_ASSET_RECEIVE_KEY_UUID = "uuid";

    private AssetReceivePresenter assetReceivePresenter = new AssetReceivePresenter();

    private FragmentAssetReceiveBinding binding;

    private String hasAssetValue;

    public static AssetReceiveFragment newInstance(String uuid) {
        AssetReceiveFragment fragment = new AssetReceiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ASSET_RECEIVE_KEY_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetReceivePresenter.setView(this);
        assetReceivePresenter.setUuid(getArguments().getString(ARG_ASSET_RECEIVE_KEY_UUID));
        assetReceivePresenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_receive, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.swipeRefresh.setColorSchemeResources(
                R.color.red600, R.color.green600, R.color.blue600, R.color.orange600);
        binding.swipeRefresh.setOnRefreshListener(assetReceivePresenter.onSwipeRefresh());
        binding.receiverAmount.addTextChangedListener(assetReceivePresenter.textWatcher());
        binding.publicKey.setOnClickListener(assetReceivePresenter.onPublicKeyTextClicked());
    }

    @Override
    public void onStart() {
        super.onStart();
        assetReceivePresenter.onStart();
    }

    @Override
    public void onStop() {
        assetReceivePresenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        assetReceivePresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean isRefreshing() {
        return binding.swipeRefresh.isRefreshing();
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        binding.swipeRefresh.setRefreshing(refreshing);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
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
    public String getPublicKey() {
        return binding.publicKey.getText().toString();
    }

    @Override
    public void setPublicKey(String publicKey) {
        binding.publicKey.setText(publicKey);
    }

    @Override
    public void invalidate() {
        binding.qrCode.invalidate();
    }

    @Override
    public void setQR(Bitmap qr) {
        binding.qrCode.setImageBitmap(qr);
    }

    @Override
    public String getHasAssetValue() {
        return hasAssetValue;
    }

    @Override
    public void setHasAssetValue(String value) {
        hasAssetValue = value;
        binding.pocketMoney.setText(getString(R.string.has_asset_amount, hasAssetValue));
    }

    @Override
    public void onNavigationItemClicked() {
        // nothing
    }
}
