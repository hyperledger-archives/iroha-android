package io.soramitsu.irohaandroid.entity;

import com.google.gson.annotations.SerializedName;

public class TransactionEntity {
    @SerializedName(value = "asset-uuid")
    public String assetUuid;
    @SerializedName(value = "alias")
    public String assetName;
    public OperationParameterEntity params;
    public String signature;
    public long timestamp;

    public class OperationParameterEntity {
        public String command;
        public String value;
        public String sender;
        public String receiver;
    }
}
