package io.soramitsu.iroha.model;

import java.io.Serializable;

public class TransferQRParameter implements Serializable {
    public static final String QR_TEXT_DEFAULT = "{\"account\":\"\",\"amount\":0}";
    public String account;
    public int amount;
}
