package jp.co.soramitsu.iroha.android.sample;

import android.content.Context;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Completable;
import io.reactivex.Single;
import iroha.protocol.BlockOuterClass;
import iroha.protocol.CommandServiceGrpc;
import iroha.protocol.Endpoint;
import iroha.protocol.Queries;
import iroha.protocol.QueryServiceGrpc;
import iroha.protocol.Responses;
import jp.co.soramitsu.iroha.android.ByteVector;
import jp.co.soramitsu.iroha.android.Keypair;
import jp.co.soramitsu.iroha.android.ModelCrypto;
import jp.co.soramitsu.iroha.android.ModelProtoQuery;
import jp.co.soramitsu.iroha.android.ModelProtoTransaction;
import jp.co.soramitsu.iroha.android.ModelQueryBuilder;
import jp.co.soramitsu.iroha.android.ModelTransactionBuilder;
import jp.co.soramitsu.iroha.android.UnsignedQuery;
import jp.co.soramitsu.iroha.android.UnsignedTx;

import static jp.co.soramitsu.iroha.android.sample.Constants.CREATOR;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.PRIV_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.PUB_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.QUERY_COUNTER;
import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.TX_COUNTER;


public class IrohaConnection {

    private final ModelCrypto crypto = new ModelCrypto();
    private final ModelTransactionBuilder txBuilder = new ModelTransactionBuilder();
    private final ModelQueryBuilder queryBuilder = new ModelQueryBuilder();
    private final ModelProtoTransaction protoTxHelper = new ModelProtoTransaction();
    private final ModelProtoQuery protoQueryHelper = new ModelProtoQuery();
    private final ManagedChannel channel;

    public IrohaConnection(Context context) {
        channel = ManagedChannelBuilder.forAddress(context.getString(R.string.iroha_url),
                context.getResources().getInteger(R.integer.iroha_port)).usePlaintext(true).build();
    }

    public Completable execute(String username, String details) {
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
            // Set account details
            UnsignedTx setDetailsTransaction = txBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
                    .createdTime(BigInteger.valueOf(currentTime))
                    .txCounter(BigInteger.valueOf(TX_COUNTER))
                    .setAccountDetail(username + "@" + DOMAIN_ID, "myFirstDetail", details)
                    .build();

            // sign transaction and get its binary representation (Blob)
            txblob = protoTxHelper.signAndAddSignature(setDetailsTransaction, userKeys).blob();

            // Convert ByteVector to byte array
            bs = toByteArray(txblob);
            // create proto object
            try {
                protoTx = BlockOuterClass.Transaction.parseFrom(bs);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            // Send transaction to iroha
            stub = CommandServiceGrpc.newBlockingStub(channel);
            stub.torii(protoTx);

            // Check if it was successful
            if (!isTransactionSuccessful(stub, setDetailsTransaction)) {
                emitter.onError(new RuntimeException("Transaction failed"));
            }
            // Query the result
            UnsignedQuery firstName = queryBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
                    .createdTime(BigInteger.valueOf(currentTime))
                    .getAccountDetail(username + "@" + DOMAIN_ID)
                    .build();
            ByteVector queryBlob = protoQueryHelper.signAndAddSignature(firstName, userKeys).blob();
            byte bquery[] = toByteArray(queryBlob);

            Queries.Query protoQuery = null;
            try {
                protoQuery = Queries.Query.parseFrom(bquery);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            QueryServiceGrpc.QueryServiceBlockingStub queryStub = QueryServiceGrpc.newBlockingStub(channel);
            Responses.QueryResponse queryResponse = queryStub.find(protoQuery);

            emitter.onComplete();
        });
    }

    private byte[] toByteArray(ByteVector blob) {
        byte bs[] = new byte[(int) blob.size()];
        for (int i = 0; i < blob.size(); ++i) {
            bs[i] = (byte) blob.get(i);
        }
        return bs;
    }

    private boolean isTransactionSuccessful(CommandServiceGrpc.CommandServiceBlockingStub stub, UnsignedTx utx) {
        ByteVector txhash = utx.hash().blob();
        byte bshash[] = toByteArray(txhash);

        Endpoint.TxStatusRequest request = Endpoint.TxStatusRequest.newBuilder().setTxHash(ByteString.copyFrom(bshash)).build();

        Iterator<Endpoint.ToriiResponse> features = stub.statusStream(request);

        Endpoint.ToriiResponse response = null;
        while (features.hasNext()) {
            response = features.next();
        }

        return response.getTxStatus() == Endpoint.TxStatus.COMMITTED;
    }
}