package io.soramitsu.irohaandroid.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.soramitsu.irohaandroid.domain.entity.response.ResponseObject;

public class Asset extends ResponseObject {
    @SerializedName(value = "asset-uuid")
    public String uuid;
    public String name;
    public String domain;
    public String creator;
    public String signature;
    public String value;
    public long timestamp;
}
