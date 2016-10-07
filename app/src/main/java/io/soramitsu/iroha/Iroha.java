package io.soramitsu.iroha;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import io.soramitsu.iroha.models.Asset;
import io.soramitsu.iroha.models.Domain;
import io.soramitsu.iroha.models.History;
import io.soramitsu.iroha.models.IrohaUser;
import io.soramitsu.iroha.models.ResponseObject;
import io.soramitsu.iroha.models.apis.IrohaUserClient;
import io.soramitsu.iroha.models.apis.TransactionClient;


/**
 * Wrapper class of the Iroha API.<br>
 * This class is Singleton and Builder pattern.
 */
public class Iroha {
    private static Iroha iroha = new Iroha(IrohaUserClient.newInstance(), TransactionClient.newInstance());

    private String endpoint;
    private IrohaUserClient userClient;
    private TransactionClient transactionClient;

    static class Builder {
        String endpoint;

        public Builder baseUrl(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Iroha build() {
            if (endpoint == null) {
                throw new NullPointerException();
            }
            iroha.endpoint = this.endpoint;
            return iroha;
        }
    }

    private Iroha(IrohaUserClient userClient, TransactionClient transactionClient) {
        this.userClient = userClient;
        this.transactionClient = transactionClient;
    }

    public static Iroha getInstance() {
        return iroha;
    }

    public IrohaUser register(PublicKey publicKey, String name) {
        IrohaUser user = null;
        try {
            user = userClient.register(endpoint, publicKey, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public IrohaUser getAccountInfo(String uuid) {
        IrohaUser user = null;
        try {
            user = userClient.findAccountInfo(endpoint, uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Domain registerDomain(String name, KeyPair keyPair) {
        Domain domain = null;
        try {
            domain = transactionClient.registerDomain(endpoint, name, keyPair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return domain;
    }

    public Asset createAsset(String name, String domain, KeyPair keyPair) {
        Asset asset = null;
        try {
            asset = transactionClient.registerAsset(endpoint, name, domain, keyPair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return asset;
    }

    public List<Domain> getDomains() {
        List<Domain> domains = new ArrayList<>();
        try {
            domains = transactionClient.findDomains(endpoint);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return domains;
    }

    public List<Asset> getAssets(String domain) {
        List<Asset> assets = new ArrayList<>();
        try {
            assets = transactionClient.findAssets(endpoint, domain);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assets;
    }

    public ResponseObject operationAsset(String assetUuid, String command,
                                                int amount, String receiver, KeyPair keyPair) {
        ResponseObject response = null;
        try {
            response = transactionClient.operation(endpoint, assetUuid, command, amount, receiver, keyPair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public History getHistory(String uuid) {
        History history = null;
        try {
            history = transactionClient.history(endpoint, uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public History getHistory(String domain, String asset) {
        History history = null;
        try {
            history = transactionClient.history(endpoint, domain, asset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public ResponseObject sendMessage(String messageBody, String receiver, KeyPair keyPair) {
        ResponseObject response = null;
        try {
            response = transactionClient.sendMessage(endpoint, messageBody, receiver, keyPair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
