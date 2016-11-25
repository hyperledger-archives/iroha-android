package io.soramitsu.irohaandroid.domain.repository;

import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import io.soramitsu.irohaandroid.domain.entity.response.DomainListResponse;
import io.soramitsu.irohaandroid.domain.entity.response.ResponseObject;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface DomainRepository {
    <T1 extends DomainRegisterRequest, T2 extends ResponseObject> Observable<T2> register(T1 body);

    <T extends DomainListResponse> Observable<T> domains();

    interface DomainService {
        @POST(value = "/domain/register")
        <T1 extends DomainRegisterRequest, T2 extends ResponseObject> Observable<T2> register(@Body T1 body);

        @GET(value = "/domain/list")
        <T extends DomainListResponse> Observable<T> domains();
    }
}
