package jp.co.soramitsu.iroha.android.sample.main.history;

import java.util.Date;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Transaction {
    public final long id;
    public final Date date;
    public final String username;
    public final long amount;
}