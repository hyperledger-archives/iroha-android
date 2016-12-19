package io.soramitsu.irohaandroid.net.dataset.reqest;

import com.google.gson.annotations.SerializedName;

import static io.soramitsu.irohaandroid.model.Transaction.OperationParameter;

public class AssetOperationRequest {
    @SerializedName(value = "asset-uuid")
    public String uuid;
    public OperationParameter params;
    public String signature;
    public long timestamp;
}
