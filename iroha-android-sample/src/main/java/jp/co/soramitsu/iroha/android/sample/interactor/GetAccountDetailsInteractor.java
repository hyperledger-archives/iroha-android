package jp.co.soramitsu.iroha.android.sample.interactor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.Queries;
import iroha.protocol.QueryServiceGrpc;
import iroha.protocol.Responses;
import jp.co.soramitsu.iroha.android.ByteVector;
import jp.co.soramitsu.iroha.android.Keypair;
import jp.co.soramitsu.iroha.android.ModelProtoQuery;
import jp.co.soramitsu.iroha.android.ModelQueryBuilder;
import jp.co.soramitsu.iroha.android.UnsignedQuery;
import jp.co.soramitsu.iroha.android.sample.Constants;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.QUERY_COUNTER;

public class GetAccountDetailsInteractor extends SingleInteractor<String, Void> {

    private final ModelQueryBuilder modelQueryBuilder = new ModelQueryBuilder();
    private final ModelProtoQuery protoQueryHelper = new ModelProtoQuery();
    private final PreferencesUtil preferenceUtils;

    @Inject
    ManagedChannel channel;

    @Inject
    GetAccountDetailsInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                PreferencesUtil preferenceUtils) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    protected Single<String> build(Void v) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            Keypair userKeys = preferenceUtils.retrieveKeys();
            String username = preferenceUtils.retrieveUsername();

            UnsignedQuery accountDetails = modelQueryBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
                    .createdTime(BigInteger.valueOf(currentTime))
                    .getAccountDetail(username + "@" + DOMAIN_ID)
                    .build();
            ByteVector queryBlob = protoQueryHelper.signAndAddSignature(accountDetails, userKeys).blob();
            byte bquery[] = toByteArray(queryBlob);

            Queries.Query protoQuery = null;
            try {
                protoQuery = Queries.Query.parseFrom(bquery);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            QueryServiceGrpc.QueryServiceBlockingStub queryStub = QueryServiceGrpc.newBlockingStub(channel);
            Responses.QueryResponse queryResponse = queryStub.find(protoQuery);

            JsonElement jsonElement = new Gson().fromJson(queryResponse.getAccountDetailResponse().getDetail(), JsonObject.class).get(username + "@" + DOMAIN_ID);;
            String detail = jsonElement != null ? jsonElement.getAsJsonObject().get(Constants.ACCOUNT_DETAILS).getAsString() : "";

            emitter.onSuccess(detail);
        });
    }
}
