package io.soramitsu.irohaandroid.domain.entity.reqest;

import com.google.gson.annotations.SerializedName;

import io.soramitsu.irohaandroid.domain.entity.OperationParameter;

public class AssetOperationRequest {
    @SerializedName(value = "asset-uuid")
    public String uuid;
    public OperationParameter params;
    public String signature;
    public long timestamp;
}
