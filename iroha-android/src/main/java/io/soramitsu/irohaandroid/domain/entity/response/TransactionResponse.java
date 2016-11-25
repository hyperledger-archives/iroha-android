package io.soramitsu.irohaandroid.domain.entity.response;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

import io.soramitsu.irohaandroid.domain.entity.Transaction;

public class TransactionResponse extends ResponseObject {
    public String uuid;
    @SerializedName("history")
    public Collection<Transaction> transactions;
}
