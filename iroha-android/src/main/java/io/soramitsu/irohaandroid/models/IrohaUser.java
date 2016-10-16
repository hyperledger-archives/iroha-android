package io.soramitsu.irohaandroid.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Iroha account.
 */
public class IrohaUser extends ResponseObject {
    private String uuid;
    @SerializedName("alias")
    private String name;
    private List<Asset> assets;

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

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
}
