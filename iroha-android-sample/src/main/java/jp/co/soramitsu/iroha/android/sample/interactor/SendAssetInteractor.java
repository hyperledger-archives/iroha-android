package jp.co.soramitsu.iroha.android.sample.interactor;


import com.google.protobuf.InvalidProtocolBufferException;
import com.orhanobut.logger.Logger;

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
import jp.co.soramitsu.iroha.android.ModelProtoTransaction;
import jp.co.soramitsu.iroha.android.ModelTransactionBuilder;
import jp.co.soramitsu.iroha.android.UnsignedTx;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CREATOR;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.PRIV_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.PUB_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.TX_COUNTER;

public class SendAssetInteractor extends CompletableInteractor<String[]> {

    private final ManagedChannel channel;
    private final ModelTransactionBuilder txBuilder = new ModelTransactionBuilder();
    private final ModelProtoTransaction protoTxHelper = new ModelProtoTransaction();
    private final PreferencesUtil preferenceUtils;
    private final ModelCrypto crypto;

    @Inject
    SendAssetInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                        @Named(ApplicationModule.UI) Scheduler uiScheduler,
                        ManagedChannel managedChannel, PreferencesUtil preferencesUtil, ModelCrypto crypto) {
        super(jobScheduler, uiScheduler);
        this.channel = managedChannel;
        this.preferenceUtils = preferencesUtil;
        this.crypto = crypto;
    }

    @Override
    protected Completable build(String[] data) {
        return Completable.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            Keypair userKeys = preferenceUtils.retrieveKeys();
            Keypair adminKeys = crypto.convertFromExisting(PUB_KEY, PRIV_KEY);
            String username = preferenceUtils.retrieveUsername();

            //Sending asset
            UnsignedTx sendAssetTx = txBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
                    .createdTime(BigInteger.valueOf(currentTime))
                    .transferAsset(username + "@" + DOMAIN_ID, data[0] + "@" + DOMAIN_ID, "irh#" + DOMAIN_ID, "initial" , data[1])
                    .build();

            ByteVector txblob = protoTxHelper.signAndAddSignature(sendAssetTx, userKeys).blob();
            byte[] bsq = toByteArray(txblob);
            BlockOuterClass.Transaction protoTx = null;

            try {
                protoTx = BlockOuterClass.Transaction.parseFrom(bsq);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            CommandServiceGrpc.CommandServiceBlockingStub stub = CommandServiceGrpc.newBlockingStub(channel)
                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            stub.torii(protoTx);

            // Check if it was successful
            if (!isTransactionSuccessful(stub, sendAssetTx)) {
                emitter.onError(new RuntimeException("Transaction failed"));
            } else {
                emitter.onComplete();
            }
        });
    }
}