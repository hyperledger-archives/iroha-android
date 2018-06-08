package jp.co.soramitsu.iroha.android.sample.interactor;

import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import iroha.protocol.BlockOuterClass;
import iroha.protocol.CommandServiceGrpc;
import jp.co.soramitsu.iroha.android.ByteVector;
import jp.co.soramitsu.iroha.android.Keypair;
import jp.co.soramitsu.iroha.android.ModelProtoTransaction;
import jp.co.soramitsu.iroha.android.ModelTransactionBuilder;
import jp.co.soramitsu.iroha.android.UnsignedTx;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.ACCOUNT_DETAILS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;

public class SetAccountDetailsInteractor extends CompletableInteractor<String> {

    private final ManagedChannel channel;
    private final ModelTransactionBuilder txBuilder = new ModelTransactionBuilder();
    private final PreferencesUtil preferenceUtils;
    private ModelProtoTransaction protoTxHelper;

    @Inject
    SetAccountDetailsInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                ManagedChannel managedChannel, PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.channel = managedChannel;
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(String details) {
        return Completable.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            Keypair userKeys = preferenceUtils.retrieveKeys();
            String username = preferenceUtils.retrieveUsername();

            UnsignedTx setDetailsTransaction = txBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
                    .createdTime(BigInteger.valueOf(currentTime))
                    .setAccountDetail(username + "@" + DOMAIN_ID, ACCOUNT_DETAILS, details)
                    .build();

            protoTxHelper = new ModelProtoTransaction(setDetailsTransaction);
            ByteVector txblob = protoTxHelper.signAndAddSignature(userKeys).finish().blob();
            byte[] bs = toByteArray(txblob);
            BlockOuterClass.Transaction protoTx = null;

            try {
                protoTx = BlockOuterClass.Transaction.parseFrom(bs);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            CommandServiceGrpc.CommandServiceBlockingStub stub = CommandServiceGrpc.newBlockingStub(channel)
                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            stub.torii(protoTx);

            // Check if it was successful
            if (!isTransactionSuccessful(stub, setDetailsTransaction)) {
                emitter.onError(new RuntimeException("Transaction failed"));
            } else {
                emitter.onComplete();
            }
        });
    }
}