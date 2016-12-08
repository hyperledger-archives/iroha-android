package io.soramitsu.iroha.view;

import io.soramitsu.iroha.model.TransactionHistory;

public interface TransactionHistoryView extends LoadingView {
    boolean isRefreshing();

    void setRefreshing(boolean refreshing);

    void setRefreshEnable(boolean enable);

    void showError(String error);

    TransactionHistory getTransaction();

    void renderTransactionHistory(TransactionHistory transactionHistory);
}
