package io.soramitsu.irohaandroid.data.net;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;

import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.data.entity.AssetEntity;
import io.soramitsu.irohaandroid.data.entity.AssetListEntity;
import io.soramitsu.irohaandroid.data.entity.DomainEntity;
import io.soramitsu.irohaandroid.data.entity.DomainListEntity;
import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public class RestApi {
    private Retrofit retrofit;

    public RestApi(OkHttpClient client) {
        final Iroha iroha = Iroha.getInstance();
        final String baseUrl = iroha.getBaseUrl();

        if (baseUrl == null) {
            throw new NullPointerException("baseUrl should be NOT null. You have to set endpoint that you use.");
        }

        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public AccountService accountService() {
        return retrofit.create(AccountService.class);
    }

    public DomainService domainService() {
        return retrofit.create(DomainService.class);
    }

    public AssetService assetService() {
        return retrofit.create(AssetService.class);
    }

    public TransactionService transactionService() {
        return retrofit.create(TransactionService.class);
    }

    public interface AccountService {
        @POST(value = "/account/register")
        Observable<AccountEntity> register(@Body AccountRegisterRequest body);

        @GET(value = "/account")
        Observable<AccountEntity> userInfo(@Query("uuid") String uuid);
    }

    public interface DomainService {
        @POST(value = "/domain/register")
        Observable<DomainEntity> register(@Body DomainRegisterRequest body);

        @GET(value = "/domain/list")
        Observable<DomainListEntity> domains();
    }

    public interface AssetService {
        @POST(value = "/asset/create")
        Observable<AssetEntity> create(@Body AssetRegisterRequest body);

        @GET(value = "{domain}/asset/list")
        Observable<AssetListEntity> assets(@Path(value = "domain") String domain);

        @POST(value = "/asset/operation")
        Observable<AssetEntity> operation(@Body AssetOperationRequest body);
    }

    public interface TransactionService {
        @GET(value = "/history/transaction")
        Observable<TransactionListEntity> history(@Query(value = "uuid") String uuid);

        @GET(value = "/history/{domain}/{asset}/transaction")
        Observable<TransactionListEntity> history(
                @Query(value = "uuid") String uuid,
                @Path(value = "domain") String domain,
                @Path(value = "asset") String asset
        );
    }
}
