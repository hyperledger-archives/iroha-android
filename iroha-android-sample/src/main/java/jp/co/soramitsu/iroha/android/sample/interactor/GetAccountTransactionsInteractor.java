package jp.co.soramitsu.iroha.android.sample.interactor;

import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.BlockOuterClass;
import iroha.protocol.Queries;
import iroha.protocol.QueryServiceGrpc;
import iroha.protocol.Responses;
import jp.co.soramitsu.iroha.android.ByteVector;
import jp.co.soramitsu.iroha.android.Keypair;
import jp.co.soramitsu.iroha.android.ModelProtoQuery;
import jp.co.soramitsu.iroha.android.ModelQueryBuilder;
import jp.co.soramitsu.iroha.android.UnsignedQuery;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.android.sample.main.history.Transaction;

import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.QUERY_COUNTER;

public class GetAccountTransactionsInteractor extends SingleInteractor<List<Transaction>, Void> {

    private final ModelQueryBuilder modelQueryBuilder = new ModelQueryBuilder();
    private final ModelProtoQuery protoQueryHelper = new ModelProtoQuery();
    private final PreferencesUtil preferenceUtils;

    @Inject
    ManagedChannel channel;

    @Inject
    GetAccountTransactionsInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                     @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                     PreferencesUtil preferenceUtils) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    protected Single<List<Transaction>> build(Void v) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            Keypair userKeys = preferenceUtils.retrieveKeys();
            String username = preferenceUtils.retrieveUsername();

            UnsignedQuery accountBalanceQuery = modelQueryBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
                    .createdTime(BigInteger.valueOf(currentTime))
                    .getAccountTransactions(username + "@" + DOMAIN_ID)
                    .build();
            ByteVector queryBlob = protoQueryHelper.signAndAddSignature(accountBalanceQuery, userKeys).blob();
            byte bquery[] = toByteArray(queryBlob);

            Queries.Query protoQuery = null;
            try {
                protoQuery = Queries.Query.parseFrom(bquery);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            QueryServiceGrpc.QueryServiceBlockingStub queryStub = QueryServiceGrpc.newBlockingStub(channel);
            Responses.QueryResponse queryResponse = queryStub.find(protoQuery);

            List<Transaction> transactions = new ArrayList<>();

            for (BlockOuterClass.Transaction transaction : queryResponse.getTransactionsResponse().getTransactionsList()) {
                Date date = new Date();
                date.setTime(transaction.getPayload().getCreatedTime());

                Long amount = Long.parseLong(getIntBalance(transaction.getPayload().getCommands(0).getTransferAsset().getAmount()));

                String sender = transaction.getPayload().getCreatorAccountId();
                String receiver = transaction.getPayload().getCommands(0).getTransferAsset().getDestAccountId();
                String currentAccount = username + "@" + DOMAIN_ID;

                if (sender.equals(currentAccount)) {
                    amount = -amount;
                }

                String user = sender;
                if (sender.equals(currentAccount)) {
                    user = receiver;
                }

                transactions.add(new Transaction(0, date, user.split("@")[0], amount));
            }
            emitter.onSuccess(transactions);
        });
    }
}
