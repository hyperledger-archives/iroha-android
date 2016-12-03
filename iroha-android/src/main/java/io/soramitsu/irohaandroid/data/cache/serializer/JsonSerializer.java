package io.soramitsu.irohaandroid.data.cache.serializer;

import com.google.gson.Gson;

import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;

public class JsonSerializer {
    private final Gson gson = new Gson();

    public String serialize(AccountEntity accountEntity) {
        return gson.toJson(accountEntity, AccountEntity.class);
    }

    public String serialize(TransactionListEntity transactionListEntity) {
        return gson.toJson(transactionListEntity, TransactionListEntity.class);
    }

    public AccountEntity deserializeToAccountEntity(String jsonString) {
        return gson.fromJson(jsonString, AccountEntity.class);
    }

    public TransactionListEntity deserializeToTransactionListEntity(String jsonString) {
        return gson.fromJson(jsonString, TransactionListEntity.class);
    }
}
