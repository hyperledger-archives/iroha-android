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

import android.app.Activity;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import io.soramitsu.irohaandroid.async.DataSet;
import io.soramitsu.irohaandroid.async.IrohaAsyncTask;
import io.soramitsu.irohaandroid.async.IrohaParallelAsyncTask;
import io.soramitsu.irohaandroid.callback.Callback;
import io.soramitsu.irohaandroid.callback.Func2;
import io.soramitsu.irohaandroid.callback.Func3;
import io.soramitsu.irohaandroid.callback.Function;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.model.Asset;
import io.soramitsu.irohaandroid.model.Domain;
import io.soramitsu.irohaandroid.model.Transaction;
import io.soramitsu.irohaandroid.service.AccountService;
import io.soramitsu.irohaandroid.service.AssetService;
import io.soramitsu.irohaandroid.service.DomainService;
import io.soramitsu.irohaandroid.service.TransactionService;

public class Iroha {
    private static final String TAG = Iroha.class.getSimpleName();
    private static final int COUNT_TWO_PARALLEL_TASK = 2;
    private static final int COUNT_THREE_PARALLEL_TASK = 3;

    private static Iroha iroha;

    private final AccountService accountService = new AccountService();
    private final DomainService domainService = new DomainService();
    private final AssetService assetService = new AssetService();
    private final TransactionService transactionService = new TransactionService();

    private final Map<String, IrohaAsyncTask<?>> asyncTaskMap = new HashMap<>();

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

    public <T> void runAsyncTask(final String tag, final Callback<T> callback,
                                 final Function<T> func, boolean clear) {
        IrohaAsyncTask<?> asyncTask = asyncTaskMap.get(tag);
        if (asyncTask == null || clear) {
            asyncTask = new IrohaAsyncTask<T>(callback) {
                @Override
                protected T onBackground() throws Exception {
                    return func.call();
                }
            };
            asyncTaskMap.put(tag, asyncTask);
        }
        asyncTask.execute();
    }

    public boolean cancelIrohaAsyncTask(final String tag) {
        IrohaAsyncTask asyncTask = asyncTaskMap.get(tag);
        return asyncTask != null && asyncTask.cancel(true);
    }

    public <T1, T2, R> void runParallelAsyncTask(
            final Activity activity,
            final Function<? extends T1> f1,
            final Function<? extends T2> f2,
            final Func2<? super T1, ? super T2, ? extends R> func2,
            final Callback<R> callback) {

        final CountDownLatch countDownLatch = new CountDownLatch(COUNT_TWO_PARALLEL_TASK);
        new Thread(new Runnable() {
            DataSet<T1, T2, Void> dataSet;

            @Override
            public void run() {
                try {
                    dataSet = new DataSet<>();

                    new IrohaParallelAsyncTask<>(dataSet, f1, countDownLatch).execute();
                    new IrohaParallelAsyncTask<>(dataSet, f2, countDownLatch).execute();

                    countDownLatch.await();
                    Log.d(TAG, "run: all async task finished.");

                    if (activity == null) {
                        Log.d(TAG, "Background Thread run: success");
                        callback.onSuccessful(func2.call(dataSet.getT1(), dataSet.getT2()));
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "UI Thread run: success");
                                callback.onSuccessful(func2.call(dataSet.getT1(), dataSet.getT2()));
                            }
                        });
                    }
                } catch (final Exception e) {
                    if (activity == null) {
                        Log.d(TAG, "Background Thread run: failure");
                        callback.onFailure(e);
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "UI Thread run: failure");
                                callback.onFailure(e);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public <T1, T2, T3, R> void runParallelAsyncTask(
            final Activity activity,
            final Function<? extends T1> f1,
            final Function<? extends T2> f2,
            final Function<? extends T3> f3,
            final Func3<? super T1, ? super T2, ? super T3, ? extends R> func3,
            final Callback<R> callback) {

        final CountDownLatch countDownLatch = new CountDownLatch(COUNT_THREE_PARALLEL_TASK);
        new Thread(new Runnable() {
            DataSet<T1, T2, T3> dataSet;

            @Override
            public void run() {
                try {
                    dataSet = new DataSet<>();

                    new IrohaParallelAsyncTask<>(dataSet, f1, countDownLatch).execute();
                    new IrohaParallelAsyncTask<>(dataSet, f2, countDownLatch).execute();
                    new IrohaParallelAsyncTask<>(dataSet, f3, countDownLatch).execute();

                    countDownLatch.await();
                    Log.d(TAG, "run: all async task finished.");

                    if (activity == null) {
                        Log.d(TAG, "Background Thread run: success");
                        callback.onSuccessful(func3.call(dataSet.getT1(), dataSet.getT2(), dataSet.getT3()));
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "UI Thread run: success");
                                callback.onSuccessful(func3.call(dataSet.getT1(), dataSet.getT2(), dataSet.getT3()));
                            }
                        });
                    }
                } catch (final Exception e) {
                    if (activity == null) {
                        Log.d(TAG, "Background Thread run: failure");
                        callback.onFailure(e);
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "UI Thread run: failure");
                                callback.onFailure(e);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public Function<Account> registerAccountFunction(final String publicKey, final String alias) {
        return new Function<Account>() {
            @Override
            public Account call() throws Exception {
                return accountService.register(publicKey, alias);
            }
        };
    }

    public Function<Account> findAccountFunction(final String uuid) {
        return new Function<Account>() {
            @Override
            public Account call() throws Exception {
                return accountService.findAccount(uuid);
            }
        };
    }

    public Function<Domain> registerDomainFunction(
            final String name, final String owner, final String signature) {

        return new Function<Domain>() {
            @Override
            public Domain call() throws Exception {
                return domainService.register(name, owner, signature);
            }
        };
    }

    public Function<List<Domain>> findDomainsFunction(final int limit, final int offset) {

        return new Function<List<Domain>>() {
            @Override
            public List<Domain> call() throws Exception {
                return domainService.findDomains(limit, offset);
            }
        };
    }

    public Function<Asset> createAssetFunction(
            final String name, final String domain, final String creator,
            final String signature, final long timestamp) {

        return new Function<Asset>() {
            @Override
            public Asset call() throws Exception {
                return assetService.create(name, domain, creator, signature, timestamp);
            }
        };
    }

    public Function<List<Asset>> findAssetsFunction(
            final String domain, final int limit, final int offset) {

        return new Function<List<Asset>>() {
            @Override
            public List<Asset> call() throws Exception {
                return assetService.findAssets(domain, limit, offset);
            }
        };
    }

    public Function<Boolean> operateAssetFunction(
            final String assetUuid, final String command, final String value,
            final String sender, final String receiver, final String signature,
            final long timestamp) {

        return new Function<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return assetService.operation(assetUuid, command, value, sender, receiver, signature, timestamp);
            }
        };
    }

    public Function<List<Transaction>> findTransactionHistoryFunction(
            final String uuid, final int limit, final int offset) {

        return new Function<List<Transaction>>() {
            @Override
            public List<Transaction> call() throws Exception {
                return transactionService.findHistory(uuid, limit, offset);
            }
        };
    }

    public Function<List<Transaction>> findTransactionHistoryFunction(
            final String uuid, final String domain, final String asset,
            final int limit, final int offset) {

        return new Function<List<Transaction>>() {
            @Override
            public List<Transaction> call() throws Exception {
                return transactionService.findHistory(uuid, domain, asset, limit, offset);
            }
        };
    }

    public void registerAccount(final String publicKey, final String alias,
                                Callback<Account> callback) {

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

    public void createAsset(final String name, final String domain, final String creator,
                            final String signature, final long timestamp, Callback<Asset> callback) {
        createAssetAsyncTask = new IrohaAsyncTask<Asset>(callback) {
            @Override
            protected Asset onBackground() throws Exception {
                return assetService.create(name, domain, creator, signature, timestamp);
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
                               final String sender, final String receiver, final String signature,
                               final long timestamp, Callback<Boolean> callback) {
        operationAssetAsyncTask = new IrohaAsyncTask<Boolean>(callback) {
            @Override
            protected Boolean onBackground() throws Exception {
                return assetService.operation(assetUuid, command, value, sender, receiver, signature, timestamp);
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
