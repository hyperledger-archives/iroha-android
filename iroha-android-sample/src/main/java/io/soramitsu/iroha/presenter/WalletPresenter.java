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
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import click.kobaken.rxirohaandroid.Iroha;
import click.kobaken.rxirohaandroid.callback.Callback;
import click.kobaken.rxirohaandroid.callback.Func2;
import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.Transaction;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.NetworkNotConnectedException;
import io.soramitsu.iroha.model.TransactionHistory;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.WalletView;
import io.soramitsu.iroha.view.fragment.WalletFragment;

public class WalletPresenter implements Presenter<WalletView> {
    public static final String TAG = WalletPresenter.class.getSimpleName();

    private static final String IROHA_TASK_TAG_USER_INFO_ON_WALLET = "UserInfoOnWallet";
    private static final String IROHA_TASK_TAG_TRANSACTION = "Transaction";

    private WalletView walletView;

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
        transactionRunnable = new Runnable() {
            @Override
            public void run() {
                transactionHistory(WalletFragment.RefreshState.SWIPE_UP);
            }
        };
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
        Iroha iroha = Iroha.getInstance();
        iroha.cancelAsyncTask(IROHA_TASK_TAG_USER_INFO_ON_WALLET);
        iroha.cancelAsyncTask(IROHA_TASK_TAG_TRANSACTION);
    }

    @Override
    public void onDestroy() {
        // nothing
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

        if (uuid == null || uuid.isEmpty()) {
            uuid = getUuid();
        }

        Iroha iroha = Iroha.getInstance();
        iroha.runParallelAsyncTask(
                walletView.getActivity(),
                IROHA_TASK_TAG_USER_INFO_ON_WALLET,
                iroha.findAccountFunction(uuid),
                IROHA_TASK_TAG_TRANSACTION,
                iroha.findTransactionHistoryFunction(uuid, 30, 0),
                collectFunc(),
                callback()
        );
    }

    private Func2<Account, List<Transaction>, TransactionHistory> collectFunc() {
        return new Func2<Account, List<Transaction>, TransactionHistory>() {
            @Override
            public TransactionHistory call(Account account, List<Transaction> transactions) {
                TransactionHistory transactionHistory = new TransactionHistory();
                if (account != null && account.assets != null && !account.assets.isEmpty()) {
                    transactionHistory.value = account.assets.get(0).value;
                }
                transactionHistory.histories = transactions;
                return transactionHistory;
            }
        };
    }

    private Callback<TransactionHistory> callback() {
        return new Callback<TransactionHistory>() {
            @Override
            public void onSuccessful(TransactionHistory result) {
                if (walletView.isRefreshing()) {
                    walletView.setRefreshing(false);
                }

                walletView.hideProgress();

                walletView.renderTransactionHistory(result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                fail(throwable);
            }
        };
    }

    private void fail(Throwable throwable) {
        if (walletView.isRefreshing()) {
            walletView.setRefreshing(false);
        }

        walletView.hideProgress();

        final Context context = walletView.getContext();
        if (NetworkUtil.isOnline(walletView.getContext())) {
            walletView.showError(
                    ErrorMessageFactory.create(context, throwable)
            );
        } else {
            walletView.showError(
                    ErrorMessageFactory.create(context, new NetworkNotConnectedException())
            );
        }
    }

    public SwipeRefreshLayout.OnRefreshListener onSwipeRefresh() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHandler.postDelayed(transactionRunnable, 1500);
            }
        };
    }

    public View.OnClickListener onRefresh() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionHistory(WalletFragment.RefreshState.EMPTY_REFRESH);
            }
        };
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

    private String getUuid() {
        final Context context = walletView.getContext();
        final String uuid;
        try {
            uuid = Account.getUuid(context);
        } catch (NoSuchPaddingException | UnrecoverableKeyException | NoSuchAlgorithmException
                | KeyStoreException | InvalidKeyException | IOException e) {
            walletView.showError(ErrorMessageFactory.create(context, e));
            return null;
        }
        return uuid;
    }

}
