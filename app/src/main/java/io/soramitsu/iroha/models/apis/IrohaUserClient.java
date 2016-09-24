package io.soramitsu.iroha.models.apis;

import io.soramitsu.iroha.models.IrohaUser;


/**
 * Wrapper class of the Iroha account API.
 */
public class IrohaUserClient extends BaseClient {

    /**
     * To register iroha account.
     *
     * @param publicKey User's ed25519 public Key (Base64)
     * @param userName  User name
     * @return User info(user name, uuid, etc.)<br>
     *     If account duplicated that error response returned.
     */
    public IrohaUser register(String publicKey, String userName) {
        return null;
    }

    /**
     * To get the Iroha account info in accordance with uuid.
     *
     * @param uuid ID for identifies the Iroha account (sha3)
     * @return User info(user name, uuid, etc.)<br>
     *     If account duplicated that error response returned.
     */
    public IrohaUser findUserInfo(String uuid) {
        return null;
    }
}
