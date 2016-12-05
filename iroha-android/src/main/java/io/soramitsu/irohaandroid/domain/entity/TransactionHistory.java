package io.soramitsu.irohaandroid.domain.entity;

import java.io.Serializable;
import java.util.List;

public class TransactionHistory implements Serializable {
    public String value;
    public List<Transaction> histories;
}
