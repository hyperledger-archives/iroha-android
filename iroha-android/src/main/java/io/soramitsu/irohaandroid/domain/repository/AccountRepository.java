package io.soramitsu.irohaandroid.domain.repository;

import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.domain.entity.response.AccountInfoResponse;
import io.soramitsu.irohaandroid.domain.entity.response.AccountRegisterResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface AccountRepository {
    <T1 extends AccountRegisterRequest, T2 extends AccountRegisterResponse> Observable<T2> register(T1 body);

    <T extends AccountInfoResponse> Observable<T> userInfo(String uuid);

    interface AccountService {
        @POST(value = "/account/register")
        <T1 extends AccountRegisterRequest, T2 extends AccountRegisterResponse> Observable<T2> register(@Body T1 body);

        @GET(value = "/account")
        <T extends AccountInfoResponse> Observable<T> userInfo(@Query("uuid") String uuid);
    }
}
