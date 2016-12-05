package io.soramitsu.irohaandroid.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable {
    @SerializedName(value = "asset-uuid")
    public String assetUuid;
    @SerializedName(value = "name")
    public String assetName;
    public OperationParameter params;
    public String signature;
    public long timestamp;

    public static class OperationParameter implements Serializable {
        public String command;
        public String value;
        public String sender;
        public String receiver;
    }
}
