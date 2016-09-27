package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import io.soramitsu.iroha.models.IrohaUser;
import okhttp3.Response;

import static io.soramitsu.iroha.utils.DigestUtil.getPublicKeyEncodedBase64;
import static io.soramitsu.iroha.utils.NetworkUtil.ENDPOINT_URL;
import static io.soramitsu.iroha.utils.NetworkUtil.STATUS_OK;
import static io.soramitsu.iroha.utils.NetworkUtil.STATUS_BAD;
import static io.soramitsu.iroha.utils.NetworkUtil.get;
import static io.soramitsu.iroha.utils.NetworkUtil.post;
import static io.soramitsu.iroha.utils.NetworkUtil.responseToString;


/**
 * Wrapper class of the Iroha account API.
 */
public class IrohaUserClient {
    private static IrohaUserClient userClient;

    private Gson gson;

    private IrohaUserClient(Gson gson) {
        this.gson = gson;
    }

    /**
     * To generate new instance. (This is singleton)
     *
     * @return Instance of IrohaUserClient
     */
    public static IrohaUserClient newInstance() {
        if (userClient == null) {
            userClient = new IrohaUserClient(new Gson());
        }
        return userClient;
    }

    /**
     * 【POST】To register iroha account.
     *
     * @param publicKey User's ed25519 public Key (Base64)
     * @param name      User name
     * @return User info(user name, uuid, etc.)<br>
     *     If account duplicated that error response returned.
     */
    public IrohaUser register(PublicKey publicKey, String name) throws IOException {
        final Map<String, Object> body = new HashMap<>();
        body.put("publicKey", getPublicKeyEncodedBase64(publicKey));
        body.put("alias", name);
        body.put("timestamp", System.currentTimeMillis() / 1000L);

        Response response = post(ENDPOINT_URL + "/account/register", gson.toJson(body));
        IrohaUser user;
        switch (response.code()) {
            case STATUS_OK:
                user = gson.fromJson(responseToString(response), IrohaUser.class);
                user.setName(name);
                break;
            case STATUS_BAD:
                user = gson.fromJson(responseToString(response), IrohaUser.class);
                break;
            default:
                user = new IrohaUser();
                user.setStatus(response.code());
                user.setMessage(response.message());
        }
        return user;
    }

    /**
     * 【GET】To get the Iroha account info in accordance with uuid.
     *
     * @param uuid ID for identifies the Iroha account (sha3)
     * @return User info(user name, uuid, etc.)<br>
     *     If account duplicated that error response returned.
     */
    public IrohaUser findAccountInfo(String uuid) throws IOException {
        Response response = get(ENDPOINT_URL + "/account?uuid=" + uuid);
        IrohaUser user;
        switch (response.code()) {
            case STATUS_OK:
            case STATUS_BAD:
                user = gson.fromJson(responseToString(response), IrohaUser.class);
                break;
            default:
                user = new IrohaUser();
                user.setStatus(response.code());
                user.setMessage(response.message());
        }
        return user;
    }
}
