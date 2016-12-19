package io.soramitsu.irohaandroid.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionListEntity extends BaseEntity {
    public String uuid;
    @SerializedName("history")
    public List<TransactionEntity> transactionEntities;
}
