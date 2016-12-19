package io.soramitsu.iroha.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.RowTransactionListBinding;
import io.soramitsu.irohaandroid.model.Transaction;

public class TransactionListAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> transactionHistory;
    private String publicKey;

    public TransactionListAdapter(Context context, List<Transaction> transactionHistory, String publicKey) {
        this.context = context;
        this.transactionHistory = transactionHistory;
        this.publicKey = publicKey;
    }

    @Override
    public int getCount() {
        if (transactionHistory == null) {
            return 0;
        }
        return transactionHistory.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Transaction getItem(int position) {
        if (transactionHistory.size() <= position || position < 0) {
            return null;
        }
        return transactionHistory.get(position);
    }

    public void setItems(List<Transaction> transactionHistory) {
        ArrayList<Transaction> sortedTransactionList = new ArrayList<>(transactionHistory);
        Collections.sort(sortedTransactionList, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t1.timestamp < t2.timestamp ? 1 : t1.timestamp == t2.timestamp ? 0 : -1;
            }
        });
        this.transactionHistory = sortedTransactionList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowTransactionListBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_transaction_list, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (RowTransactionListBinding) convertView.getTag();
        }
        binding.setTransaction(getItem(position));
        binding.setPublicKey(publicKey);
        binding.setContext(context);
        return convertView;
    }
}
