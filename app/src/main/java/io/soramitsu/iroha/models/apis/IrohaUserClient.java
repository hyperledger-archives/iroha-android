package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.soramitsu.iroha.models.IrohaUser;
import okhttp3.Response;

import static io.soramitsu.iroha.utils.NetworkUtil.ENDPOINT_URL;
import static io.soramitsu.iroha.utils.NetworkUtil.STATUS_OK;
import static io.soramitsu.iroha.utils.NetworkUtil.STATUS_BAD;
import static io.soramitsu.iroha.utils.NetworkUtil.STATUS_NOT_FOUND;
import static io.soramitsu.iroha.utils.NetworkUtil.get;
import static io.soramitsu.iroha.utils.NetworkUtil.post;


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
     * @param userName  User name
     * @return User info(user name, uuid, etc.)<br>
     *     If account duplicated that error response returned.
     */
    public IrohaUser register(String publicKey, String userName) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("publicKey", publicKey);
        body.put("alias", userName);
        body.put("timestamp", System.currentTimeMillis());

        Response response = post(ENDPOINT_URL + "/account/register", gson.toJson(body));
        return gson.fromJson(responseToString(response), IrohaUser.class);
    }

    /**
     * 【GET】To get the Iroha account info in accordance with uuid.
     *
     * @param uuid ID for identifies the Iroha account (sha3)
     * @return User info(user name, uuid, etc.)<br>
     *     If account duplicated that error response returned.
     */
    public IrohaUser findUserInfo(String uuid) throws IOException {
        Response response = get(ENDPOINT_URL + "/account?uuid=" + uuid);
        return gson.fromJson(responseToString(response), IrohaUser.class);
    }

    private String responseToString(Response response) throws IOException {
        String result;
        switch (response.code()) {
            case STATUS_OK:
            case STATUS_BAD:
                result = response.body().string();
                break;
            case STATUS_NOT_FOUND:
                result = "";
                break;
            default:
                result = "";
        }
        return result;
    }
}
