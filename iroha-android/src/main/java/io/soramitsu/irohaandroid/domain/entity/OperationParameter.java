package io.soramitsu.irohaandroid.domain.entity;

import java.io.Serializable;

public class OperationParameter implements Serializable {
    public String command;
    public String value;
    public String sender;
    public String receiver;
}
