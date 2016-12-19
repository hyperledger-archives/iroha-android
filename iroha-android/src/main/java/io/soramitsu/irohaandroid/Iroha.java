/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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

    private IrohaAsyncTask<Account> accountRegisterAsyncTask;
    private IrohaAsyncTask<Account> findAccountAsyncTask;
    private IrohaAsyncTask<Domain> domainRegisterAsyncTask;
    private IrohaAsyncTask<List<Domain>> findDomainsAsyncTask;
    private IrohaAsyncTask<Asset> createAssetAsyncTask;
    private IrohaAsyncTask<List<Asset>> findAssetsAsyncTask;
    private IrohaAsyncTask<Boolean> operationAssetAsyncTask;
    private IrohaAsyncTask<List<Transaction>> findTransactionHistoryAsyncTask;

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
        accountRegisterAsyncTask = new IrohaAsyncTask<Account>(callback) {
            @Override
            protected Account onBackground() throws Exception {
                return accountService.register(publicKey, alias);
            }
        };
        accountRegisterAsyncTask.execute();
    }

    public boolean cancelRegisterAccount() {
        return accountRegisterAsyncTask != null && accountRegisterAsyncTask.cancel(true);
    }

    public void findAccount(final String uuid, final Callback<Account> callback) {
        findAccountAsyncTask = new IrohaAsyncTask<Account>(callback) {
            @Override
            protected Account onBackground() throws Exception {
                return accountService.findAccount(uuid);
            }
        };
        findAccountAsyncTask.execute();
    }

    public boolean cancelFindAccount() {
        return findAccountAsyncTask != null && findAccountAsyncTask.cancel(true);
    }

    public void registerDomain(final String name, final String owner,
                               final String signature, Callback<Domain> callback) {
        domainRegisterAsyncTask = new IrohaAsyncTask<Domain>(callback) {
            @Override
            protected Domain onBackground() throws Exception {
                return domainService.register(name, owner, signature);
            }
        };
        domainRegisterAsyncTask.execute();
    }

    public boolean cancelRegisterDomain() {
        return domainRegisterAsyncTask != null && domainRegisterAsyncTask.cancel(true);
    }

    public void findDomains(final int limit, final int offset, Callback<List<Domain>> callback) {
        findDomainsAsyncTask = new IrohaAsyncTask<List<Domain>>(callback) {
            @Override
            protected List<Domain> onBackground() throws Exception {
                return domainService.findDomains(limit, offset);
            }
        };
        findDomainsAsyncTask.execute();
    }

    public boolean cancelFindDomains() {
        return findDomainsAsyncTask != null && findDomainsAsyncTask.cancel(true);
    }

    public void createAsset(final String name, final String domain,
                            final String creator, final String signature, Callback<Asset> callback) {
        createAssetAsyncTask = new IrohaAsyncTask<Asset>(callback) {
            @Override
            protected Asset onBackground() throws Exception {
                return assetService.create(name, domain, creator, signature);
            }
        };
        createAssetAsyncTask.execute();
    }

    public boolean cancelCreateAsset() {
        return createAssetAsyncTask != null && createAssetAsyncTask.cancel(true);
    }

    public void findAssets(final String domain, final int limit, final int offset, Callback<List<Asset>> callback) {
        findAssetsAsyncTask = new IrohaAsyncTask<List<Asset>>(callback) {
            @Override
            protected List<Asset> onBackground() throws Exception {
                return assetService.findAssets(domain, limit, offset);
            }
        };
        findAssetsAsyncTask.execute();
    }

    public boolean cancelFindAssets() {
        return findAssetsAsyncTask != null && findAssetsAsyncTask.cancel(true);
    }

    public void operationAsset(final String assetUuid, final String command, final String value,
                               final String sender, final String receiver, Callback<Boolean> callback) {
       operationAssetAsyncTask =  new IrohaAsyncTask<Boolean>(callback) {
            @Override
            protected Boolean onBackground() throws Exception {
                return assetService.operation(assetUuid, command, value, sender, receiver);
            }
        };
        operationAssetAsyncTask.execute();
    }

    public boolean cancelOperationAsset() {
        return operationAssetAsyncTask != null && operationAssetAsyncTask.cancel(true);
    }

    public void findTransactionHistory(final String uuid, final int limit, final int offset,
                                       Callback<List<Transaction>> callback) {
        findTransactionHistoryAsyncTask = new IrohaAsyncTask<List<Transaction>>(callback) {
            @Override
            protected List<Transaction> onBackground() throws Exception {
                return transactionService.findHistory(uuid, limit, offset);
            }
        };
        findTransactionHistoryAsyncTask.execute();
    }

    public void findTransactionHistory(final String uuid, final String domain, final String asset,
                                       final int limit, final int offset, Callback<List<Transaction>> callback) {
        findTransactionHistoryAsyncTask = new IrohaAsyncTask<List<Transaction>>(callback) {
            @Override
            protected List<Transaction> onBackground() throws Exception {
                return transactionService.findHistory(uuid, domain, asset, limit, offset);
            }
        };
        findTransactionHistoryAsyncTask.execute();
    }

    public boolean cancelFindTransactionHistory() {
        return findTransactionHistoryAsyncTask != null && findTransactionHistoryAsyncTask.cancel(true);
    }
}
