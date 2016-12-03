package io.soramitsu.irohaandroid.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

public class TransactionListEntity extends BaseEntity {
    public String uuid;
    @SerializedName("history")
    public Collection<TransactionEntity> transactionEntities;
}
