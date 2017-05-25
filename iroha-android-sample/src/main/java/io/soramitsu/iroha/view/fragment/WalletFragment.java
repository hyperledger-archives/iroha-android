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
import android.widget.Toast;

import java.util.ArrayList;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentWalletBinding;
import io.soramitsu.iroha.model.AccountInfo;
import io.soramitsu.iroha.presenter.WalletPresenter;
import io.soramitsu.iroha.view.WalletView;
import io.soramitsu.iroha.view.activity.MainActivity;
import io.soramitsu.iroha.view.adapter.TransactionListAdapter;
import io.soramitsu.irohaandroid.model.KeyPair;

public class WalletFragment extends Fragment
        implements WalletView, MainActivity.MainActivityListener {
    public static final String TAG = WalletFragment.class.getSimpleName();

    private static final String ARG_WALLET_KEY_UUID = "uuid";

    private WalletPresenter walletPresenter = new WalletPresenter();

    private FragmentWalletBinding binding;
    private TransactionListAdapter transactionListAdapter;

    private AccountInfo accountInfo;

    public enum RefreshState {
        SWIPE_UP, RE_CREATED_FRAGMENT, EMPTY_REFRESH
    }

    public static WalletFragment newInstance(String uuid) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WALLET_KEY_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walletPresenter.setView(this);
        walletPresenter.setUuid(getArguments().getString(ARG_WALLET_KEY_UUID));
        walletPresenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.swipeRefresh.setColorSchemeResources(
                R.color.red600, R.color.green600, R.color.blue600, R.color.orange600);
        binding.swipeRefresh.setOnRefreshListener(walletPresenter.onSwipeRefresh());
        binding.transactionList.setOnScrollListener(walletPresenter.onTransactionListScroll());
        binding.transactionList.setEmptyView(binding.emptyView);
        binding.emptyText.setOnClickListener(walletPresenter.onRefresh());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String publicKey = null;
        try {
            publicKey = KeyPair.getKeyPair(getContext()).publicKey;
        } catch (Exception e) {
            Log.e(TAG, "onActivityCreated: ", e);
        }

        transactionListAdapter = new TransactionListAdapter(
                getContext(),
                new ArrayList<>(),
                publicKey
        );
        binding.transactionList.setAdapter(transactionListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        walletPresenter.onStart();
        walletPresenter.transactionHistory(RefreshState.RE_CREATED_FRAGMENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        walletPresenter.onResume();
    }

    @Override
    public void onPause() {
        walletPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        walletPresenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        walletPresenter.onDestroy();
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
    public void setRefreshEnable(final boolean enable) {
        binding.swipeRefresh.setEnabled(enable);
    }

    @Override
    public void showError(final String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public AccountInfo getTransaction() {
        return accountInfo;
    }

    @Override
    public void renderTransactionHistory(final AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
        if (accountInfo.value != null) {
            binding.pocketMoney.setText(getString(R.string.has_asset_amount, accountInfo.value));
        }
        transactionListAdapter.setItems(this.accountInfo.transactionHistory);
        transactionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.transactionList.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
        binding.transactionList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNavigationItemClicked() {
        binding.transactionList.setSelection(0);
    }
}
