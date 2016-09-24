package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.soramitsu.iroha.models.IrohaUser;
import okhttp3.OkHttpClient;


/**
 * Wrapper class of the Iroha account API.
 */
public class IrohaUserClient extends BaseClient {

    public IrohaUserClient(OkHttpClient okHttpClient, Gson gson) {
        super(okHttpClient, gson);
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

        String result = post(ENDPOINT_URL + "/account/register", gson.toJson(body));
        return gson.fromJson(result, IrohaUser.class);
    }

    /**
     * 【GET】To get the Iroha account info in accordance with uuid.
     *
     * @param uuid ID for identifies the Iroha account (sha3)
     * @return User info(user name, uuid, etc.)<br>
     *     If account duplicated that error response returned.
     */
    public IrohaUser findUserInfo(String uuid) throws IOException {
        String result = get(ENDPOINT_URL + "/account?uuid=" + uuid);
        return gson.fromJson(result, IrohaUser.class);
    }
}
