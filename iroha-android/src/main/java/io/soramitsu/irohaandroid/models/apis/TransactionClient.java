package io.soramitsu.irohaandroid.models.apis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kobaken0029.ed25519.Ed25519;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.soramitsu.irohaandroid.models.Asset;
import io.soramitsu.irohaandroid.models.Domain;
import io.soramitsu.irohaandroid.models.History;
import io.soramitsu.irohaandroid.models.ResponseObject;
import okhttp3.Response;

import static com.kobaken0029.ed25519.Ed25519.sign;
import static io.soramitsu.irohaandroid.utils.DigestUtil.sha3_256;
import static io.soramitsu.irohaandroid.utils.NetworkUtil.STATUS_BAD;
import static io.soramitsu.irohaandroid.utils.NetworkUtil.STATUS_OK;
import static io.soramitsu.irohaandroid.utils.NetworkUtil.get;
import static io.soramitsu.irohaandroid.utils.NetworkUtil.post;
import static io.soramitsu.irohaandroid.utils.NetworkUtil.responseToString;


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

    /**
     * 【POST】To register iroha domain.
     *
     * @param endpoint  Iroha API endpoint
     * @param name      Domain name
     * @param keyPair   Public key and Private key sets in your device
     * @return Registered domain (domain name, creator, etc.)
     */
    public Domain registerDomain(String endpoint, String name, Ed25519.KeyPair keyPair) throws IOException {
        final long timestamp = System.currentTimeMillis() / 1000L;
        final String publicKey = keyPair.getPublicKey();
        final String message = sha3_256("timestamp:" + timestamp + ",owner:" + publicKey + ",name:" + name);

        final Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("owner", publicKey);
        body.put("signature", "signature:" + message);
        body.put("timestamp", timestamp);

        Response response = post(endpoint + "/domain/register", gson.toJson(body));
        Domain domain;
        switch (response.code()) {
            case STATUS_OK:
                domain = gson.fromJson(responseToString(response), Domain.class);
                domain.setName(name);
                break;
            case STATUS_BAD:
                domain = gson.fromJson(responseToString(response), Domain.class);
                break;
            default:
                domain = new Domain();
                domain.setStatus(response.code());
                domain.setMessage(response.message());
        }
        return domain;
    }

    /**
     * 【POST】To register iroha asset.
     *
     * @param endpoint  Iroha API endpoint
     * @param name      Asset name
     * @param domain    Domain name
     * @param keyPair   Public key and Private key sets in your device
     * @return Created asset (asset name, domain, creator, etc.)
     */
    public Asset registerAsset(String endpoint, String name, String domain, Ed25519.KeyPair keyPair) throws IOException {
        final long timestamp = System.currentTimeMillis() / 1000L;
        final String publicKey = keyPair.getPublicKey();
        final String message = sha3_256("timestamp:" + timestamp + ",creator:" + publicKey + ",name:" + name);

        final Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("domain", domain);
        body.put("creator", publicKey);
        body.put("signature", "signature:" + message);
        body.put("timestamp", timestamp);

        Response response = post(endpoint + "/asset/create", gson.toJson(body));
        Asset asset;
        switch (response.code()) {
            case STATUS_OK:
                asset = gson.fromJson(responseToString(response), Asset.class);
                asset.setName(name);
                asset.setDomain(domain);
                asset.setCreator(publicKey);
                break;
            case STATUS_BAD:
                asset = gson.fromJson(responseToString(response), Asset.class);
                break;
            default:
                asset = new Asset();
                asset.setStatus(response.code());
                asset.setMessage(response.message());
        }
        return asset;
    }

    /**
     * 【GET】To find all domains.
     *
     * @param endpoint Iroha API endpoint
     * @return All domains
     */
    public List<Domain> findDomains(String endpoint) throws IOException {
        Response response = get(endpoint + "/domain/list");
        List<Domain> domains = new ArrayList<>();
        switch (response.code()) {
            case STATUS_OK:
                Type type = new TypeToken<List<Domain>>(){}.getType();
                domains = gson.fromJson(responseToString(response), type);
                break;
            case STATUS_BAD:
                break;
            default:
        }
        return domains;
    }

    /**
     * 【GET】To find all assets.
     *
     * @param endpoint Iroha API endpoint
     * @return All assets
     */
    public List<Asset> findAssets(String endpoint, String domain) throws IOException {
        Response response = get(endpoint + "/asset/list/" + domain);
        List<Asset> assets = new ArrayList<>();
        switch (response.code()) {
            case STATUS_OK:
                Type type = new TypeToken<List<Asset>>(){}.getType();
                assets = gson.fromJson(responseToString(response), type);
                break;
            case STATUS_BAD:
                break;
            default:
        }
        return assets;
    }

    /**
     * 【POST】To operate iroha asset for command.
     *
     * @param endpoint  Iroha API endpoint
     * @param assetUuid target asset uuid
     * @param command   operate command (transfer)
     * @param keyPair   Public key and Private key sets in your device
     * @return As a result of operation. (response code and message)
     */
    public ResponseObject operation(String endpoint, String assetUuid, String command,
                                    int amount, String receiver, Ed25519.KeyPair keyPair) throws IOException {
        final long timestamp = System.currentTimeMillis() / 1000L;
        final Map<String, Object> params = new HashMap<>();
        params.put("command", command);
        params.put("amount", amount);
        params.put("sender", keyPair.getPublicKey());
        params.put("receiver", receiver);
        final String message = "timestamp:" + timestamp + ",sender:" + params.get("sender")
                + ",receiver:" + receiver + ",command:" + command
                + ",amount:" + amount + ",asset-uuid:" + assetUuid;

        final Map<String, Object> body = new HashMap<>();
        body.put("asset-uuid", assetUuid);
        body.put("params", gson.toJson(params));
        body.put("signature", "signature:" + message);
        body.put("timestamp", System.currentTimeMillis() / 1000L);

        Response response = post(endpoint + "/asset/operation", gson.toJson(body));
        ResponseObject responseObject;
        switch (response.code()) {
            case STATUS_OK:
            case STATUS_BAD:
                responseObject = gson.fromJson(responseToString(response), ResponseObject.class);
                break;
            default:
                responseObject = new ResponseObject();
                responseObject.setStatus(response.code());
                responseObject.setMessage(response.message());
        }
        return responseObject;
    }

    /**
     * 【GET】To get history of transaction.
     *
     * @param endpoint  Iroha API endpoint
     * @param uuid      User's uuid
     * @return Transaction of history made by user.
     */
    public History history(String endpoint, String uuid) throws IOException {
        Response response = get(endpoint + "/history/transaction/" + uuid);
        return history(response);
    }

    /**
     * 【GET】To get history of transaction.
     *
     * @param endpoint  Iroha API endpoint
     * @param domain    Domain name
     * @param asset     Asset name
     * @return Transaction of history made by user.
     */
    public History history(String endpoint, String domain, String asset) throws IOException {
        Response response = get(endpoint + "/history/" + domain + "." + asset);
        return history(response);
    }

    private History history(Response response) throws IOException {
        History history;
        switch (response.code()) {
            case STATUS_OK:
                history = gson.fromJson(responseToString(response), History.class);
                history.setStatus(response.code());
                break;
            default:
                history = new History();
                history.setStatus(response.code());
                history.setMessage(response.message());
        }
        return history;
    }

    /**
     * 【POST】To send receiver a message.
     *
     * @param endpoint    Iroha API endpoint
     * @param messageBody Message body
     * @param receiver    Receiver's public key
     * @param keyPair     Public key and Private key sets in your device
     * @return As a result of operation. (response code and message)
     */
    public ResponseObject sendMessage(String endpoint, String messageBody,
                                      String receiver, Ed25519.KeyPair keyPair) throws IOException {
        final long timestamp = System.currentTimeMillis() / 1000L;
        final String publicKey = keyPair.getPublicKey();
        final String message = sha3_256("timestamp:" + timestamp + ",message" + messageBody + ",creator:" + publicKey);

        final Map<String, Object> body = new HashMap<>();
        body.put("message", messageBody);
        body.put("creator", publicKey);
        body.put("receiver", receiver);
        body.put("signature", "signature:" + message);
        body.put("timestamp", timestamp);

        Response response = post(endpoint + "/message", gson.toJson(body));
        ResponseObject responseObject;
        switch (response.code()) {
            case STATUS_OK:
            case STATUS_BAD:
                responseObject = gson.fromJson(responseToString(response), ResponseObject.class);
                break;
            default:
                responseObject = new ResponseObject();
                responseObject.setStatus(response.code());
                responseObject.setMessage(response.message());
        }
        return responseObject;
    }
}
