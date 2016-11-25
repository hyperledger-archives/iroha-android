package io.soramitsu.irohaandroid.domain.repository;

import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.domain.entity.response.AssetListResponse;
import io.soramitsu.irohaandroid.domain.entity.response.ResponseObject;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface AssetRepository {
    <T1 extends AssetRegisterRequest, T2 extends ResponseObject> Observable<T2> create(T1 body);

    <T extends AssetListResponse> Observable<T> assets(String domain);

    <T1 extends AssetOperationRequest, T2 extends ResponseObject> Observable<T2> operation(T1 body);

    interface AssetService {
        @POST(value = "/asset/create")
        <T1 extends AssetRegisterRequest, T2 extends ResponseObject> Observable<T2> create(@Body T1 body);

        @GET(value = "{domain}/asset/list")
        <T extends AssetListResponse> Observable<T> assets(@Path(value = "domain") String domain);

        @POST(value = "/asset/operation")
        <T1 extends AssetOperationRequest, T2 extends ResponseObject> Observable<T2> operation(@Body T1 body);
    }
}
