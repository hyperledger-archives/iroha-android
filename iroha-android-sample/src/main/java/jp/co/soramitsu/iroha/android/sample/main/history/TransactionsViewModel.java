package jp.co.soramitsu.iroha.android.sample.main.history;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


public class TransactionsViewModel extends ViewModel {

    @Getter
    private MutableLiveData<List> transactions;

    public TransactionsViewModel() {
        transactions = new MutableLiveData<>();
        transactions.setValue(new ArrayList());
    }
}