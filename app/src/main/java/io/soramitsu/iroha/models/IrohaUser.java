package io.soramitsu.iroha.models;

import com.google.gson.annotations.SerializedName;


/**
 * Iroha account.
 */
public class IrohaUser extends ResponseObject {
    private String uuid;
    @SerializedName("alias")
    private String userName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
