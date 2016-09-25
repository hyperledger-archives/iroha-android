package io.soramitsu.iroha.models;

import com.google.gson.annotations.SerializedName;


/**
 * Iroha account.
 */
public class IrohaUser extends ResponseObject {
    private String uuid;
    @SerializedName("screen_name")
    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
