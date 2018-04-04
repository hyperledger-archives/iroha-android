package jp.co.soramitsu.iroha.android.sample;

import com.google.protobuf.ByteString;

import java.util.Iterator;

import iroha.protocol.CommandServiceGrpc;
import iroha.protocol.Endpoint;
import jp.co.soramitsu.iroha.android.ByteVector;
import jp.co.soramitsu.iroha.android.UnsignedTx;

public class Utils {

    public static byte[] toByteArray(ByteVector blob) {
        byte bs[] = new byte[(int) blob.size()];
        for (int i = 0; i < blob.size(); ++i) {
            bs[i] = (byte) blob.get(i);
        }
        return bs;
    }

    public static boolean isTransactionSuccessful(CommandServiceGrpc.CommandServiceBlockingStub stub, UnsignedTx utx) {
        ByteVector txhash = utx.hash().blob();
        byte bshash[] = toByteArray(txhash);

        Endpoint.TxStatusRequest request = Endpoint.TxStatusRequest.newBuilder().setTxHash(ByteString.copyFrom(bshash)).build();

        Iterator<Endpoint.ToriiResponse> features = stub.statusStream(request);

        Endpoint.ToriiResponse response = null;
        while (features.hasNext()) {
            response = features.next();
        }
        com.orhanobut.logger.Logger.e("TAG" + response.getTxStatus());
        return response.getTxStatus() == Endpoint.TxStatus.COMMITTED;
    }

}
