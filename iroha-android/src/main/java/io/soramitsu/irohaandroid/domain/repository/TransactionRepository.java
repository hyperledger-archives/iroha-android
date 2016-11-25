package io.soramitsu.irohaandroid.domain.repository;

import io.soramitsu.irohaandroid.domain.entity.response.TransactionResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface TransactionRepository {
    interface TransactionService {
        @GET(value = "/history/transaction")
        <T extends TransactionResponse> Observable<T> history(@Query(value = "uuid") String uuid);

        @GET(value = "/history/{domain}/{asset}/transaction")
        <T extends TransactionResponse> Observable<T> history(
                @Query(value = "uuid") String uuid,
                @Path(value = "domain") String domain,
                @Path(value = "asset") String asset
        );
    }
}
