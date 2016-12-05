package io.soramitsu.irohaandroid.domain.entity.reqest;

import com.google.gson.annotations.SerializedName;

import static io.soramitsu.irohaandroid.domain.entity.Transaction.OperationParameter;

public class AssetOperationRequest {
    @SerializedName(value = "asset-uuid")
    public String uuid;
    public OperationParameter params;
    public String signature;
    public long timestamp;
}
