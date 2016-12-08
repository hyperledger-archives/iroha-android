package io.soramitsu.iroha.model;

import java.io.Serializable;
import java.util.List;

import io.soramitsu.irohaandroid.model.Transaction;

public class TransactionHistory implements Serializable {
    public String value;
    public List<Transaction> histories;
}
