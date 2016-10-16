package io.soramitsu.irohaandroid.models;

import com.google.gson.annotations.SerializedName;


/**
 * Iroha message.
 */
public class Message {
    @SerializedName("message")
    private String body;
    private String creator;
    private String receiver;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
