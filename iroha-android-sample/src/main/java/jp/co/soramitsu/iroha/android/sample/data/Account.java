package jp.co.soramitsu.iroha.android.sample.data;

import iroha.protocol.Responses;
import lombok.Getter;
import lombok.Setter;

public class Account {

    @Setter
    @Getter
    private Responses.Account irohaAccount;

    @Setter
    @Getter
    private long balance;

    public Account(Responses.Account account, long balance) {
        this.irohaAccount = account;
        this.balance = balance;
    }
}
