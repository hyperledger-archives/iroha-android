package io.soramitsu.irohaandroid.domain.repository;

import java.util.List;

import io.soramitsu.irohaandroid.domain.entity.Transaction;
import io.soramitsu.irohaandroid.domain.entity.response.TransactionResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface TransactionRepository {
    Observable<List<Transaction>> history(String uuid);

    Observable<List<Transaction>> history(String uuid, String domain, String asset);
}
