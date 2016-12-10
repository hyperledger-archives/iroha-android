package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import java.util.List;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.model.TransactionHistory;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.TransactionHistoryView;
import io.soramitsu.iroha.view.fragment.TransactionHistoryFragment;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.callback.Callback;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.model.Transaction;

public class TransactionHistoryPresenter implements Presenter<TransactionHistoryView> {
    public static final String TAG = TransactionHistoryPresenter.class.getSimpleName();

    private TransactionHistoryView transactionHistoryView;

    private Handler refreshHandler;
    private Runnable transactionRunnable;

    @Override
    public void setView(@NonNull TransactionHistoryView view) {
        transactionHistoryView = view;
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
                transactionHistory(TransactionHistoryFragment.RefreshState.SWIPE_UP);
            }
        };
    }

    @Override
    public void onResume() {
        // nothing
    }

    @Override
    public void onPause() {
        if (transactionHistoryView.isRefreshing()) {
            refreshHandler.removeCallbacks(transactionRunnable);
            transactionHistoryView.setRefreshing(false);
            transactionHistoryView.setRefreshEnable(true);
        }
    }

    @Override
    public void onStop() {
        // nothing
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public void transactionHistory(final TransactionHistoryFragment.RefreshState state) {
        switch (state) {
            case RE_CREATED_FRAGMENT:
                if (transactionHistoryView.getTransaction() == null) {
                    renderFromNetwork();
                    return;
                }

                renderFromMemory();
                break;
            case EMPTY_REFRESH:
            case SWIPE_UP:
                renderFromNetwork();
                break;
        }
    }

    private void renderFromMemory() {
        Log.d(TAG, "transactionHistory: cache in memory");
        transactionHistoryView.renderTransactionHistory(transactionHistoryView.getTransaction());
    }

    private void renderFromNetwork() {
        Log.d(TAG, "transactionHistory: fetch network or cache");

        final Context context = transactionHistoryView.getContext();
        Iroha.getInstance().findAccount(Account.getUuid(context), new Callback<Account>() {
            @Override
            public void onSuccessful(final Account account) {
                Iroha.getInstance().findTransactionHistory(account.uuid, new Callback<List<Transaction>>() {
                    @Override
                    public void onSuccessful(List<Transaction> transactions) {
                        transactionHistoryView.hideProgressDialog();

                        TransactionHistory transactionHistory = new TransactionHistory();
                        transactionHistory.value = account.assets.get(0).value; // TODO マルチアセット対応
                        transactionHistory.histories = transactions;
                        transactionHistoryView.renderTransactionHistory(transactionHistory);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        fail();
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                fail();
            }
        });
    }

    private void fail() {
        if (transactionHistoryView.isRefreshing()) {
            transactionHistoryView.setRefreshing(false);
        }

        transactionHistoryView.hideProgressDialog();

        if (NetworkUtil.isOnline(transactionHistoryView.getContext())) {
            transactionHistoryView.showError(transactionHistoryView.getContext().getString(R.string.error_message_retry_again));
        } else {
            transactionHistoryView.showError(transactionHistoryView.getContext().getString(R.string.error_message_check_network_state));
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
                transactionHistory(TransactionHistoryFragment.RefreshState.EMPTY_REFRESH);
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
                if (transactionHistoryView.isRefreshing()) {
                    return;
                }

                boolean enable = false;
                if (view.getChildCount() > 0) {
                    boolean firstItemVisible = view.getChildCount() == 0;
                    boolean topOfFirstItemVisible = view.getFirstVisiblePosition() == 0
                            && view.getChildAt(0).getTop() == view.getPaddingTop();
                    enable = firstItemVisible || topOfFirstItemVisible;
                }
                transactionHistoryView.setRefreshEnable(enable);
            }
        };
    }
}
