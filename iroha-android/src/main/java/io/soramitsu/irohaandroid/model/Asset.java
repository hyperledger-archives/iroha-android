package io.soramitsu.irohaandroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Asset implements Serializable {
    @SerializedName(value = "asset-uuid")
    public String uuid;
    public String name;
    public String domain;
    public String creator;
    public String signature;
    public String value;
    public long timestamp;
}
