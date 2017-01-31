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

package click.kobaken.rxirohaandroid;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import click.kobaken.rxirohaandroid.async.BaseIrohaAsyncTask;
import click.kobaken.rxirohaandroid.async.DataSet;
import click.kobaken.rxirohaandroid.async.IrohaAsyncTask;
import click.kobaken.rxirohaandroid.async.IrohaParallelAsyncTask;
import click.kobaken.rxirohaandroid.callback.Callback;
import click.kobaken.rxirohaandroid.callback.Func2;
import click.kobaken.rxirohaandroid.callback.Func3;
import click.kobaken.rxirohaandroid.callback.Function;
import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.Asset;
import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.model.Transaction;
import click.kobaken.rxirohaandroid.service.AccountService;
import click.kobaken.rxirohaandroid.service.AssetService;
import click.kobaken.rxirohaandroid.service.DomainService;
import click.kobaken.rxirohaandroid.service.TransactionService;

public class Iroha {
    private static final String TAG = Iroha.class.getSimpleName();
    private static final int COUNT_TWO_PARALLEL_TASK = 2;
    private static final int COUNT_THREE_PARALLEL_TASK = 3;

    private static Iroha iroha;

    private final AccountService accountService = new AccountService();
    private final DomainService domainService = new DomainService();
    private final AssetService assetService = new AssetService();
    private final TransactionService transactionService = new TransactionService();

    private final Map<String, BaseIrohaAsyncTask<?>> asyncTaskMap = new HashMap<>();

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


    /* ============ 【Web API】 from here ============  */

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

    /* ============ 【Web API】 to here ============  */


    /* ============ 【Async Task Management】 from here ============  */

    public <T> void runAsyncTask(final String tag, final Function<? extends T> func,
                                 final Callback<T> callback) {

        IrohaAsyncTask<T> asyncTask = new IrohaAsyncTask<T>(callback) {
            @Override
            protected T onBackground() throws Exception {
                return func.call();
            }
        };
        execute(tag, asyncTask);
    }

    public <T1, T2, R> void runParallelAsyncTask(
            final Activity activity,
            final String tag1,
            final Function<? extends T1> f1,
            final String tag2,
            final Function<? extends T2> f2,
            final Func2<? super T1, ? super T2, ? extends R> func2,
            final Callback<R> callback) {

        new Thread(new Runnable() {
            DataSet<T1, T2, Void> dataSet = new DataSet<>();

            @Override
            public void run() {
                try {
                    CountDownLatch countDownLatch = new CountDownLatch(COUNT_TWO_PARALLEL_TASK);

                    IrohaParallelAsyncTask<?> firstParallelAsyncTask =
                            new IrohaParallelAsyncTask<T1>(dataSet, countDownLatch) {
                                @Override
                                protected T1 onBackground() throws Exception {
                                    return f1.call();
                                }
                            };
                    execute(tag1, firstParallelAsyncTask);

                    IrohaParallelAsyncTask<?> secondParallelAsyncTask =
                            new IrohaParallelAsyncTask<T2>(dataSet, countDownLatch) {
                                @Override
                                protected T2 onBackground() throws Exception {
                                    return f2.call();
                                }
                            };
                    execute(tag2, secondParallelAsyncTask);

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
            final String tag1,
            final Function<? extends T1> f1,
            final String tag2,
            final Function<? extends T2> f2,
            final String tag3,
            final Function<? extends T3> f3,
            final Func3<? super T1, ? super T2, ? super T3, ? extends R> func3,
            final Callback<R> callback) {

        new Thread(new Runnable() {
            DataSet<T1, T2, T3> dataSet = new DataSet<>();

            @Override
            public void run() {
                try {
                    CountDownLatch countDownLatch = new CountDownLatch(COUNT_THREE_PARALLEL_TASK);

                    IrohaParallelAsyncTask<?> firstParallelAsyncTask =
                            new IrohaParallelAsyncTask<T1>(dataSet, countDownLatch) {
                                @Override
                                protected T1 onBackground() throws Exception {
                                    return f1.call();
                                }
                            };
                    execute(tag1, firstParallelAsyncTask);

                    IrohaParallelAsyncTask<?> secondParallelAsyncTask =
                            new IrohaParallelAsyncTask<T2>(dataSet, countDownLatch) {
                                @Override
                                protected T2 onBackground() throws Exception {
                                    return f2.call();
                                }
                            };
                    execute(tag2, secondParallelAsyncTask);

                    IrohaParallelAsyncTask<?> threeParallelAsyncTask =
                            new IrohaParallelAsyncTask<T3>(dataSet, countDownLatch) {
                                @Override
                                protected T3 onBackground() throws Exception {
                                    return f3.call();
                                }
                            };
                    execute(tag3, threeParallelAsyncTask);

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

    public boolean cancelAsyncTask(final String tag) {
        Log.d(TAG, "cancelAsyncTask: " + tag);
        BaseIrohaAsyncTask asyncTask = asyncTaskMap.get(tag);
        return asyncTask != null && asyncTask.cancel(true);
    }

    private <C extends BaseIrohaAsyncTask<?>> void execute(
            @NonNull final String tag,
            @NonNull C newAsyncTask) {

        asyncTaskMap.put(tag, newAsyncTask);
        execute(newAsyncTask);
    }

    private void execute(@NonNull BaseIrohaAsyncTask<?> asyncTask) {
        if (asyncTask instanceof IrohaAsyncTask<?>) {
            IrohaAsyncTask<?> irohaAsyncTask = (IrohaAsyncTask<?>) asyncTask;
            irohaAsyncTask.execute();
        } else if (asyncTask instanceof IrohaParallelAsyncTask<?>) {
            IrohaParallelAsyncTask<?> irohaParallelAsyncTask = (IrohaParallelAsyncTask<?>) asyncTask;
            irohaParallelAsyncTask.execute();
        }
    }

    /* ============ 【Async Task Management】 to here ============  */

}
