package io.soramitsu.iroha.model;

import java.io.Serializable;

public class TransferQRParameter implements Serializable {
    public static final String QR_TEXT_DEFAULT = "{\"type\":\"trans\",\"account\":\"\",\"value\":0}";

    public String type;
    public String alias;
    public String account;
    public int value;
}
