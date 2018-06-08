package jp.co.soramitsu.iroha.android.sample.interactor;

import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

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
import jp.co.soramitsu.iroha.android.ModelCrypto;
import jp.co.soramitsu.iroha.android.ModelProtoQuery;
import jp.co.soramitsu.iroha.android.ModelQueryBuilder;
import jp.co.soramitsu.iroha.android.UnsignedQuery;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CREATOR;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.PRIV_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.PUB_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.QUERY_COUNTER;

public class GetAccountInteractor extends SingleInteractor<Responses.Account, String> {

    private final ModelQueryBuilder modelQueryBuilder = new ModelQueryBuilder();
    private final ModelCrypto crypto;
    private final ManagedChannel channel;
    private ModelProtoQuery protoQueryHelper;

    @Inject
    GetAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                         @Named(ApplicationModule.UI) Scheduler uiScheduler,
                         ModelCrypto crypto, ManagedChannel channel) {
        super(jobScheduler, uiScheduler);
        this.crypto = crypto;
        this.channel = channel;
    }

    @Override
    protected Single<Responses.Account> build(String accountId) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            Keypair adminKeys = crypto.convertFromExisting(PUB_KEY, PRIV_KEY);

            // GetAccount
            UnsignedQuery query = modelQueryBuilder
                    .createdTime(BigInteger.valueOf(currentTime))
                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
                    .creatorAccountId(CREATOR)
                    .getAccount(accountId + "@" + DOMAIN_ID)
                    .build();


            // sign transaction and get its binary representation (Blob)
            protoQueryHelper = new ModelProtoQuery(query);
            ByteVector queryBlob = protoQueryHelper.signAndAddSignature(adminKeys).finish().blob();
            byte bquery[] = toByteArray(queryBlob);

            Queries.Query protoQuery = null;
            try {
                protoQuery = Queries.Query.parseFrom(bquery);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            QueryServiceGrpc.QueryServiceBlockingStub queryStub = QueryServiceGrpc.newBlockingStub(channel)
                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Responses.QueryResponse queryResponse = queryStub.find(protoQuery);

            emitter.onSuccess(queryResponse.getAccountResponse().getAccount());
        });
    }
}