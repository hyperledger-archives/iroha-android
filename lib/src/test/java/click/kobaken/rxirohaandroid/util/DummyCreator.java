package click.kobaken.rxirohaandroid.util;

import java.util.ArrayList;
import java.util.List;

import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.Asset;
import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.model.Transaction;
import click.kobaken.rxirohaandroid.model.Transaction.OperationParameter;
import click.kobaken.rxirohaandroid.model.TransactionHistory;

public class DummyCreator {

    private DummyCreator() {
    }

    public static Account newAccount(int seed) {
        return new Account() {{
            String seedString = String.valueOf(seed);
            uuid = seedString;
            alias = seedString;
            assets = newAssets(seed);
        }};
    }

    public static List<Asset> newAssets(int seed) {
        return new ArrayList<Asset>() {{
            for (int i = 0; i < seed; i++) {
                add(newAsset(i));
            }
        }};
    }

    public static Asset newAsset(int seed) {
        return new Asset() {{
            String seedString = String.valueOf(seed);

            uuid = seedString;
            name = seedString;
            domain = seedString;
            creator = seedString;
            signature = seedString;
            value = seedString;
            timestamp = seed;
        }};
    }

    public static List<Domain> newDomains(int seed) {
        return new ArrayList<Domain>() {{
            for (int i = 0; i < seed; i++) {
                add(newDomain(i));
            }
        }};
    }

    public static Domain newDomain(int seed) {
        return new Domain() {{
            String seedString = String.valueOf(seed);

            name = seedString;
            owner = seedString;
            signature = seedString;
            timestamp = seed;
        }};
    }

    public static List<Transaction> newTransactionList(int seed) {
        return new ArrayList<Transaction>() {{
            for (int i = 0; i < seed; i++) {
                add(newTransaction(i));
            }
        }};
    }

    public static Transaction newTransaction(int seed) {
        return new Transaction() {{
            String seedString = String.valueOf(seed);

            assetUuid = seedString;
            assetName = seedString;
            params = newOperationParameter(seed);
            signature = seedString;
        }};
    }

    public static OperationParameter newOperationParameter(int seed) {
        return new OperationParameter() {{
            String seedString = String.valueOf(seed);

            command = seedString;
            value = seedString;
            sender = seedString;
            receiver = seedString;
            timestamp = seed;
        }};
    }

    public static TransactionHistory newTransactionHistory(int seed) {
        return new TransactionHistory() {{
            history = newTransactionList(seed);
        }};
    }
}
