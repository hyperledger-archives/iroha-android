package jp.co.soramitsu.iroha.android.sample.main.history;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class TransactionDiffChecker extends DiffUtil.Callback {
    private final List oldTransactions;
    private final List newTransactions;

    TransactionDiffChecker(List oldTransactions, List newTransactions) {
        this.oldTransactions = oldTransactions;
        this.newTransactions = newTransactions;
    }

    @Override
    public int getOldListSize() {
        return oldTransactions.size();
    }

    @Override
    public int getNewListSize() {
        return newTransactions.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldTransactions.get(oldItemPosition) instanceof TransactionVM
                && newTransactions.get(newItemPosition) instanceof TransactionVM ) {
            TransactionVM oldItem = (TransactionVM) oldTransactions.get(oldItemPosition);
            TransactionVM newItem = (TransactionVM) newTransactions.get(newItemPosition);
            return oldItem.id == newItem.id;
        } else {
            return oldTransactions.get(oldItemPosition).equals(newTransactions.get(newItemPosition));
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldTransactions.get(oldItemPosition) instanceof TransactionVM) {
            TransactionVM oldItem = (TransactionVM) oldTransactions.get(oldItemPosition);
            TransactionVM newItem = (TransactionVM) newTransactions.get(newItemPosition);
            return oldItem.prettyAmount.equals(newItem.prettyAmount)
                    && oldItem.prettyDate.equals(newItem.prettyDate)
                    && oldItem.username.equals(newItem.username);
        } else {
            return oldTransactions.get(oldItemPosition).equals(newTransactions.get(newItemPosition));
        }
    }
}