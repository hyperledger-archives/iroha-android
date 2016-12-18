package io.soramitsu.iroha.model;

public enum QRType {
    GIFT("gift"), TRANSFER("transfer");

    private String type;

    QRType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}