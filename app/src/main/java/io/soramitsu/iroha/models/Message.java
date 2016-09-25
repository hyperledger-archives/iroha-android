package io.soramitsu.iroha.models;

import com.google.gson.annotations.SerializedName;


/**
 * Iroha message.
 */
public class Message {
    @SerializedName("message")
    private String body;
    private String sender;
    private String receiver;
}
