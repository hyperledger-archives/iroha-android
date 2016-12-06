package io.soramitsu.iroha.model;

import java.io.Serializable;

public class TransferQRParameter implements Serializable {
    public String type;
    public String alias;
    public String account;
    public int value;
}
