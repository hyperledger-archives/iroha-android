package io.soramitsu.irohaandroid;

import java.util.List;

import io.soramitsu.irohaandroid.async.IrohaAsyncTask;
import io.soramitsu.irohaandroid.callback.Callback;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.model.Asset;
import io.soramitsu.irohaandroid.model.Domain;
import io.soramitsu.irohaandroid.model.Transaction;
import io.soramitsu.irohaandroid.service.AccountService;
import io.soramitsu.irohaandroid.service.AssetService;
import io.soramitsu.irohaandroid.service.DomainService;
import io.soramitsu.irohaandroid.service.TransactionService;

public class Iroha {
    private static Iroha iroha;

    private final AccountService accountService = new AccountService();
    private final DomainService domainService = new DomainService();
    private final AssetService assetService = new AssetService();
    private final TransactionService transactionService = new TransactionService();

    public String baseUrl;

    private Iroha(Builder builder) {
        this.baseUrl = builder.baseUrl;
        iroha = this;
    }

    public static class Builder {
        private String baseUrl;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Iroha build() {
            if (baseUrl == null) {
                throw new NullPointerException();
            }
            return new Iroha(this);
        }
    }

    public static Iroha getInstance() {
        if (iroha == null) {
            throw new NullPointerException();
        }
        return iroha;
    }

    public void registerAccount(final String publicKey, final String alias, Callback<Account> callback) {
        new IrohaAsyncTask<Account>(callback) {
            @Override
            protected Account onBackground() throws Exception {
                return accountService.register(publicKey, alias);
            }
        }.execute();
    }

    public void findAccount(final String uuid, final Callback<Account> callback) {
        new IrohaAsyncTask<Account>(callback) {
            @Override
            protected Account onBackground() throws Exception {
                return accountService.findAccount(uuid);
            }
        }.execute();
    }

    public void registerDomain(final String name, final String owner,
                               final String signature, Callback<Domain> callback) {
        new IrohaAsyncTask<Domain>(callback) {
            @Override
            protected Domain onBackground() throws Exception {
                return domainService.register(name, owner, signature);
            }
        }.execute();
    }

    public void findDomains(final int limit, final int offset, Callback<List<Domain>> callback) {
        new IrohaAsyncTask<List<Domain>>(callback) {
            @Override
            protected List<Domain> onBackground() throws Exception {
                return domainService.findDomains(limit, offset);
            }
        }.execute();
    }

    public void createAsset(final String name, final String domain,
                            final String creator, final String signature, Callback<Asset> callback) {
        new IrohaAsyncTask<Asset>(callback) {
            @Override
            protected Asset onBackground() throws Exception {
                return assetService.create(name, domain, creator, signature);
            }
        }.execute();
    }

    public void findAssets(final String domain, final int limit, final int offset, Callback<List<Asset>> callback) {
        new IrohaAsyncTask<List<Asset>>(callback) {
            @Override
            protected List<Asset> onBackground() throws Exception {
                return assetService.findAssets(domain, limit, offset);
            }
        }.execute();
    }

    public void operationAsset(final String assetUuid, final String command, final String value,
                               final String sender, final String receiver, final String signature,
                               Callback<Boolean> callback) {
        new IrohaAsyncTask<Boolean>(callback) {
            @Override
            protected Boolean onBackground() throws Exception {
                return assetService.operation(assetUuid, command, value, sender, receiver, signature);
            }
        }.execute();
    }

    public void findTransactionHistory(final String uuid, final int limit, final int offset,
                                       Callback<List<Transaction>> callback) {
        new IrohaAsyncTask<List<Transaction>>(callback) {
            @Override
            protected List<Transaction> onBackground() throws Exception {
                return transactionService.findHistory(uuid, limit, offset);
            }
        }.execute();
    }

    public void findTransactionHistory(final String uuid, final String domain, final String asset,
                                       final int limit, final int offset, Callback<List<Transaction>> callback) {
        new IrohaAsyncTask<List<Transaction>>(callback) {
            @Override
            protected List<Transaction> onBackground() throws Exception {
                return transactionService.findHistory(uuid, domain, asset, limit, offset);
            }
        }.execute();
    }
}
