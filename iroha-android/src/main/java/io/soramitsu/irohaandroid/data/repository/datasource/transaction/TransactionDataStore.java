package io.soramitsu.irohaandroid.data.repository.datasource.transaction;

import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import rx.Observable;

public interface TransactionDataStore {
    Observable<TransactionListEntity> history(String uuid);

    Observable<TransactionListEntity> history(String uuid, String domain, String asset);
}
