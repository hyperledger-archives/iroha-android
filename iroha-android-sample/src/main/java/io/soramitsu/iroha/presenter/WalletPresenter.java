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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.soramitsu.iroha.api.IrohaClient;
import io.soramitsu.iroha.entity.mapper.TransactionEntityDataMapper;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.NetworkNotConnectedException;
import io.soramitsu.iroha.model.Account;
import io.soramitsu.iroha.model.AccountInfo;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.WalletView;
import io.soramitsu.iroha.view.fragment.WalletFragment;

public class WalletPresenter implements Presenter<WalletView> {
    public static final String TAG = WalletPresenter.class.getSimpleName();

    private WalletView walletView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Handler refreshHandler;
    private Runnable transactionRunnable;

    private String uuid;

    @Override
    public void setView(@NonNull WalletView view) {
        walletView = view;
    }

    @Override
    public void onCreate() {
        // nothing
    }

    @Override
    public void onStart() {
        refreshHandler = new Handler();
        transactionRunnable = () -> transactionHistory(WalletFragment.RefreshState.SWIPE_UP);
    }

    @Override
    public void onResume() {
        // nothing
    }

    @Override
    public void onPause() {
        if (walletView.isRefreshing()) {
            refreshHandler.removeCallbacks(transactionRunnable);
            walletView.setRefreshing(false);
            walletView.setRefreshEnable(true);
        }
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

    public void transactionHistory(final WalletFragment.RefreshState state) {
        switch (state) {
            case RE_CREATED_FRAGMENT:
                if (walletView.getTransaction() == null) {
                    renderFromNetwork(state);
                    return;
                }

                renderFromMemory();
                break;
            case EMPTY_REFRESH:
            case SWIPE_UP:
                renderFromNetwork(state);
                break;
        }
    }

    private void renderFromMemory() {
        Log.d(TAG, "transactionHistory: cache in memory");
        walletView.renderTransactionHistory(walletView.getTransaction());
    }

    private void renderFromNetwork(WalletFragment.RefreshState state) {
        Log.d(TAG, "transactionHistory: fetch network or cache");

        switch (state) {
            case RE_CREATED_FRAGMENT:
            case SWIPE_UP:
                break;
            case EMPTY_REFRESH:
                walletView.showProgress();
                break;
        }

        if (TextUtils.isEmpty(uuid)) {
            uuid = Account.getUuid(walletView.getContext());
        }

        final IrohaClient irohaClient = IrohaClient.getInstance();
        Disposable disposable = Single.zip(
                irohaClient.fetchAccountInfo(uuid),
                irohaClient.fetchTx(uuid, 30, 0),
                (account, tx) -> {
                    AccountInfo accountInfo = new AccountInfo();
                    if (account != null && account.assets != null && !account.assets.isEmpty()) {
                        accountInfo.value = account.assets.get(0).value;
                    }
                    accountInfo.transactionHistory = TransactionEntityDataMapper.transform(tx);
                    return accountInfo;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError);
        compositeDisposable.add(disposable);
    }

    private void onSuccess(AccountInfo result) {
        if (walletView.isRefreshing()) {
            walletView.setRefreshing(false);
        }

        walletView.hideProgress();

        walletView.renderTransactionHistory(result);
    }

    private void onError(Throwable e) {
        if (walletView.isRefreshing()) {
            walletView.setRefreshing(false);
        }

        walletView.hideProgress();

        final Context context = walletView.getContext();
        if (NetworkUtil.isOnline(walletView.getContext())) {
            walletView.showError(ErrorMessageFactory.create(context, e));
        } else {
            walletView.showError(ErrorMessageFactory.create(context, new NetworkNotConnectedException()));
        }
    }

    public SwipeRefreshLayout.OnRefreshListener onSwipeRefresh() {
        return () -> refreshHandler.postDelayed(transactionRunnable, 1500);
    }

    public View.OnClickListener onRefresh() {
        return v -> transactionHistory(WalletFragment.RefreshState.EMPTY_REFRESH);
    }

    public AbsListView.OnScrollListener onTransactionListScroll() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // nothing
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (walletView.isRefreshing()) {
                    return;
                }

                if (view.getChildCount() == 0) {
                    walletView.setRefreshEnable(true);
                    return;
                }

                boolean enable = false;
                if (view.getChildCount() > 0) {
                    boolean firstItemVisible = view.getChildCount() == 0;
                    boolean topOfFirstItemVisible = view.getFirstVisiblePosition() == 0
                            && view.getChildAt(0).getTop() == view.getPaddingTop();
                    enable = firstItemVisible || topOfFirstItemVisible;
                }
                walletView.setRefreshEnable(enable);
            }
        };
    }
}
