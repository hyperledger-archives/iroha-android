package io.soramitsu.irohaandroid.data.entity;

import com.google.gson.annotations.SerializedName;

public class AssetEntity {
    @SerializedName(value = "asset-uuid")
    public String uuid;
    public String name;
    public String domain;
    public String creator;
    public String signature;
    public String value;
    public long timestamp;
}
