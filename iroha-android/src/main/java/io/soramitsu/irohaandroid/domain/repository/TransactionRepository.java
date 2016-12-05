package io.soramitsu.irohaandroid.domain.repository;

import java.util.List;

import io.soramitsu.irohaandroid.domain.entity.Transaction;
import rx.Observable;

public interface TransactionRepository {
    Observable<List<Transaction>> findHistory(String uuid);

    Observable<List<Transaction>> findHistory(String uuid, String domain, String asset);
}
