package io.soramitsu.irohaandroid.entity;

import com.google.gson.annotations.SerializedName;

public class AssetEntity extends BaseEntity {
    @SerializedName(value = "asset-uuid")
    public String uuid;
    public String name;
    public String domain;
    public String creator;
    public String signature;
    public String value;
    public long timestamp;
}
