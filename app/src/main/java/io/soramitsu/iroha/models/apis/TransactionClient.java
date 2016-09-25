package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import java.util.List;

import io.soramitsu.iroha.models.Asset;
import io.soramitsu.iroha.models.Domain;
import io.soramitsu.iroha.models.History;
import io.soramitsu.iroha.models.Message;
import io.soramitsu.iroha.models.Operation;
import io.soramitsu.iroha.models.ResponseObject;


/**
 * Wrapper class of the Iroha transaction API.
 */
public class TransactionClient {
    private static TransactionClient transactionClient;

    private Gson gson;

    private TransactionClient(Gson gson) {
        this.gson = gson;
    }

    /**
     * To generate new instance. (This is singleton)
     *
     * @return Instance of IrohaUserClient
     */
    public static TransactionClient newInstance() {
        if (transactionClient == null) {
            transactionClient = new TransactionClient(new Gson());
        }
        return transactionClient;
    }

    public Domain registerDomain(String name, String owner, String signature) {
        return null;
    }

    public Asset registerAsset(String name, String domain, String creator, String signature) {
        return null;
    }

    public List<Domain> findDomains() {
        return null;
    }

    public List<Asset> findAssets(String domain) {
        return null;
    }

    public ResponseObject assetOperation(String assetUuid, String signature, Operation params) {
        return null;
    }

    public History history(String uuid) {
        return null;
    }

    public History history(Domain domain, Asset asset) {
        return null;
    }

    public ResponseObject sendMessage(Message message, String signature) {
        return null;
    }
}
