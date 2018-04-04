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
import jp.co.soramitsu.iroha.android.ModelCrypto;
import jp.co.soramitsu.iroha.android.ModelProtoQuery;
import jp.co.soramitsu.iroha.android.ModelProtoTransaction;
import jp.co.soramitsu.iroha.android.ModelQueryBuilder;
import jp.co.soramitsu.iroha.android.ModelTransactionBuilder;
import jp.co.soramitsu.iroha.android.UnsignedTx;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CREATOR;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.PRIV_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.PUB_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.TX_COUNTER;
import static jp.co.soramitsu.iroha.android.sample.Utils.isTransactionSuccessful;
import static jp.co.soramitsu.iroha.android.sample.Utils.toByteArray;

public class CreateAccountInteractor extends CompletableInteractor<String> {

    private final ManagedChannel channel;
    private final ModelCrypto crypto = new ModelCrypto();
    private final ModelTransactionBuilder txBuilder = new ModelTransactionBuilder();
    private final ModelQueryBuilder queryBuilder = new ModelQueryBuilder();
    private final ModelProtoTransaction protoTxHelper = new ModelProtoTransaction();
    private final ModelProtoQuery protoQueryHelper = new ModelProtoQuery();

    @Inject
    CreateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                            @Named(ApplicationModule.UI) Scheduler uiScheduler,
                            ManagedChannel managedChannel) {
        super(jobScheduler, uiScheduler);
        this.channel = managedChannel;
    }

    @Override
    protected Completable build(String username) {
        return Completable.create(emitter -> {
            long currentTime = System.currentTimeMillis();

            Keypair userKeys = crypto.generateKeypair();
            Keypair adminKeys = crypto.convertFromExisting(PUB_KEY, PRIV_KEY);
            // Create account
            UnsignedTx createAccount = txBuilder.creatorAccountId(CREATOR)
                    .createdTime(BigInteger.valueOf(currentTime))
                    .txCounter(BigInteger.valueOf(TX_COUNTER))
                    .createAccount(username, DOMAIN_ID, userKeys.publicKey())
                    .build();

            // sign transaction and get its binary representation (Blob)
            ByteVector txblob = protoTxHelper.signAndAddSignature(createAccount, adminKeys).blob();

            // Convert ByteVector to byte array
            byte bs[] = toByteArray(txblob);

            // create proto object
            BlockOuterClass.Transaction protoTx = null;
            try {
                protoTx = BlockOuterClass.Transaction.parseFrom(bs);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            // Send transaction to iroha
            CommandServiceGrpc.CommandServiceBlockingStub stub = CommandServiceGrpc.newBlockingStub(channel)
                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            stub.torii(protoTx);

            // Check if it was successful
            if (!isTransactionSuccessful(stub, createAccount)) {
                emitter.onError(new RuntimeException("Transaction failed"));
            }
            emitter.onComplete();
        });
    }
}
