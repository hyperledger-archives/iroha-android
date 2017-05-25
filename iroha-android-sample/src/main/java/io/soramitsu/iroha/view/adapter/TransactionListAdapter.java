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

package io.soramitsu.iroha.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.RowTransactionListBinding;
import io.soramitsu.iroha.model.Transaction;

public class TransactionListAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> transactionHistory;
    private String publicKey;

    public TransactionListAdapter(Context context,
                                  List<Transaction> transactionHistory,
                                  String publicKey) {
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
        Collections.sort(sortedTransactionList, (tx1, tx2) -> {
            long ts1 = tx1.params.timestamp;
            long ts2 = tx2.params.timestamp;
            return ts1 < ts2 ? 1 : ts1 == ts2 ? 0 : -1;
        });
        this.transactionHistory = sortedTransactionList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowTransactionListBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil
                    .inflate(LayoutInflater.from(context), R.layout.row_transaction_list, parent, false);
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
