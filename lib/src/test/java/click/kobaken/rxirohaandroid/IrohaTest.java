/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.Asset;
import click.kobaken.rxirohaandroid.model.BaseModel;
import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.model.Transaction;
import click.kobaken.rxirohaandroid.model.TransactionHistory;
import click.kobaken.rxirohaandroid.net.IrohaHttpClient;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static click.kobaken.rxirohaandroid.util.ParserUtil.serialize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class IrohaTest {

    private MockWebServer mockWebServer;
    private MockResponse mockResponse;

    private Iroha.Builder irohaBuilder;

    @Before
    public void setUp() throws Exception {
        initMockServer();
        irohaBuilder = new Iroha.Builder().client(IrohaHttpClient.getInstance().get());
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void registerAccount_201() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        Account account = createAccount(Arrays.asList(createAsset(), createAsset(), createAsset()));
        mockWebServer.enqueue(mockResponse.setResponseCode(201).setBody(serialize(account)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerAccount(publicKey, alias), new DisposableObserver<Account>() {
            @Override
            public void onNext(Account value) {
                assertThat(value.uuid, is("uuid"));
                assertThat(value.alias, is("alias"));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void registerAccount_200_account_duplicated() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        BaseModel response = new BaseModel();
        response.status = 400;
        response.message = "duplicate user";
        mockWebServer.enqueue(mockResponse.setResponseCode(200).setBody(serialize(response)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerAccount(publicKey, alias), new DisposableObserver<Account>() {
            @Override
            public void onNext(Account value) {
                assertThat(value.status, is(400));
                assertThat(value.message, is("duplicate user"));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void registerAccount_404() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerAccount(publicKey, alias), new DisposableObserver<Account>() {
            @Override
            public void onNext(Account value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void findAccount_200() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        Account account = createAccount(Arrays.asList(createAsset(), createAsset(), createAsset()));
        mockWebServer.enqueue(mockResponse.setResponseCode(200).setBody(serialize(account)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerAccount(publicKey, alias), new DisposableObserver<Account>() {
            @Override
            public void onNext(Account value) {
                assertThat(value.uuid, is("uuid"));
                assertThat(value.alias, is("alias"));
                assertThat(value.assets.get(0).uuid, is(account.assets.get(0).uuid));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void findAccount_200_account_not_found() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        BaseModel response = new BaseModel();
        response.status = 400;
        response.message = "User not found";
        mockWebServer.enqueue(mockResponse.setResponseCode(200).setBody(serialize(response)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerAccount(publicKey, alias), new DisposableObserver<Account>() {
            @Override
            public void onNext(Account value) {
                assertThat(value.status, is(400));
                assertThat(value.message, is("User not found"));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void findAccount_404() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerAccount(publicKey, alias), new DisposableObserver<Account>() {
            @Override
            public void onNext(Account value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void registerDomain_201() throws Exception {
        final String name = "name";
        final String owner = "owner";
        final String signature = "signature";

        Domain domain = createDomain(name, owner, signature);
        mockWebServer.enqueue(mockResponse.setResponseCode(201).setBody(serialize(domain)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerDomain(name, owner, signature), new DisposableObserver<Domain>() {
            @Override
            public void onNext(Domain value) {
                assertThat(value.name, is(name));
                assertThat(value.owner, is(owner));
                assertThat(value.signature, is(signature));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void registerDomain_404() throws Exception {
        final String name = "name";
        final String owner = "owner";
        final String signature = "signature";

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.registerDomain(name, owner, signature), new DisposableObserver<Domain>() {
            @Override
            public void onNext(Domain value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void findDomains_200() throws Exception {
        final int limit = 30;
        final int offset = 0;

        List<Domain> domains = Arrays.asList(createDomain(), createDomain(), createDomain());
        mockWebServer.enqueue(mockResponse.setResponseCode(200).setBody(serialize(domains)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findDomains(limit, offset), new DisposableObserver<List<Domain>>() {
            @Override
            public void onNext(List<Domain> value) {
                assertThat(value.size(), is(3));
                assertThat(value.get(0).name, is("name"));
                assertThat(value.get(0).owner, is("owner"));
                assertThat(value.get(0).signature, is("signature"));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void findDomains_404() throws Exception {
        final int limit = 30;
        final int offset = 0;

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findDomains(limit, offset), new DisposableObserver<List<Domain>>() {
            @Override
            public void onNext(List<Domain> value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void createAsset_201() throws Exception {
        final String uuid = "uuid";
        final String name = "name";
        final String domain = "domain";
        final String creator = "creator";
        final String signature = "signature";

        Asset asset = createAsset(uuid, name, domain, creator, signature);
        mockWebServer.enqueue(mockResponse.setResponseCode(201).setBody(serialize(asset)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.createAsset(name, domain, creator, signature, asset.timestamp), new DisposableObserver<Asset>() {
            @Override
            public void onNext(Asset value) {
                assertThat(value.uuid, is(uuid));
                assertThat(value.name, is(name));
                assertThat(value.domain, is(domain));
                assertThat(value.creator, is(creator));
                assertThat(value.signature, is(signature));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void createAsset_404() throws Exception {
        final String name = "name";
        final String domain = "domain";
        final String creator = "creator";
        final String signature = "signature";

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.createAsset(name, domain, creator, signature, 0L), new DisposableObserver<Asset>() {
            @Override
            public void onNext(Asset value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void findAssets_200() throws Exception {
        final String domain = "domain";
        final int limit = 30;
        final int offset = 0;

        List<Asset> assets = Arrays.asList(createAsset(), createAsset(), createAsset());
        mockWebServer.enqueue(mockResponse.setResponseCode(200).setBody(serialize(assets)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findAssets(domain, limit, offset), new DisposableObserver<List<Asset>>() {
            @Override
            public void onNext(List<Asset> value) {
                assertThat(value.size(), is(3));
                assertThat(value.get(0).uuid, is("uuid"));
                assertThat(value.get(0).name, is("name"));
                assertThat(value.get(0).domain, is(domain));
                assertThat(value.get(0).signature, is("signature"));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void findAssets_404() throws Exception {
        final String domain = "domain";
        final int limit = 30;
        final int offset = 0;

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findAssets(domain, limit, offset), new DisposableObserver<List<Asset>>() {
            @Override
            public void onNext(List<Asset> value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void operateAsset_201() throws Exception {
        final String assetUuid = "asset-uuid";
        final String command = "command";
        final String value = "value";
        final String sender = "sender";
        final String receiver = "receiver";
        final String signature = "signature";

        BaseModel response = new BaseModel();
        response.status = 201;
        response.message = "Asset transfered successfully";
        mockWebServer.enqueue(mockResponse.setResponseCode(201).setBody(serialize(response)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.operateAsset(assetUuid, command, value, sender, receiver, signature, 0L), new DisposableObserver<BaseModel>() {
            @Override
            public void onNext(BaseModel value) {
                assertThat(value.status, is(201));
                assertThat(value.message, is("Asset transfered successfully"));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void operateAsset_404() throws Exception {
        final String assetUuid = "asset-uuid";
        final String command = "command";
        final String value = "value";
        final String sender = "sender";
        final String receiver = "receiver";
        final String signature = "signature";

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.operateAsset(assetUuid, command, value, sender, receiver, signature, 0L), new DisposableObserver<BaseModel>() {
            @Override
            public void onNext(BaseModel value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void findTransactionHistory_200() throws Exception {
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        TransactionHistory history = createTransactionHistory(Arrays.asList(createTransaction(), createTransaction()));
        mockWebServer.enqueue(mockResponse.setResponseCode(200).setBody(serialize(history)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findTransactionHistory(uuid, limit, offset), new DisposableObserver<TransactionHistory>() {
            @Override
            public void onNext(TransactionHistory value) {
                assertThat(value.history.size(), is(2));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void findTransactionHistory_404() throws Exception {
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findTransactionHistory(uuid, limit, offset), new DisposableObserver<TransactionHistory>() {
            @Override
            public void onNext(TransactionHistory value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    @Test
    public void findTransactionHistory_multi_asset_200() throws Exception {
        final String domain = "domain";
        final String asset = "asset";
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        TransactionHistory history = createTransactionHistory(Arrays.asList(createTransaction(), createTransaction()));
        mockWebServer.enqueue(mockResponse.setResponseCode(200).setBody(serialize(history)));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findTransactionHistory(domain, asset, uuid, limit, offset), new DisposableObserver<TransactionHistory>() {
            @Override
            public void onNext(TransactionHistory value) {
                assertThat(value.history.size(), is(2));
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void findTransactionHistory_multi_asset_404() throws Exception {
        final String domain = "domain";
        final String asset = "asset";
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        mockWebServer.enqueue(mockResponse.setResponseCode(404));

        Iroha iroha = buildIroha();

        final CountDownLatch latch = new CountDownLatch(1);
        execute(iroha.findTransactionHistory(domain, asset, uuid, limit, offset), new DisposableObserver<TransactionHistory>() {
            @Override
            public void onNext(TransactionHistory value) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {
            }
        });
        latch.await();
    }

    private void initMockServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(11262);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-type", "application/json");
        mockResponse = new MockResponse().setHeaders(Headers.of(headerMap));
    }

    private Account createAccount(List<Asset> assets, String... params) {
        int length = params.length;
        Account account = new Account();
        account.uuid = length > 0 ? params[0] : "uuid";
        account.alias = length > 1 ? params[1] : "alias";
        account.assets = assets;
        return account;
    }

    private Asset createAsset(String... params) {
        int length = params.length;
        Asset asset = new Asset();
        asset.uuid = length > 0 ? params[0] : "uuid";
        asset.name = length > 1 ? params[1] : "name";
        asset.domain = length > 2 ? params[2] : "domain";
        asset.creator = length > 3 ? params[3] : "creator";
        asset.signature = length > 4 ? params[4] : "signature";
        asset.value = length > 5 ? params[5] : "value";
        asset.timestamp = System.currentTimeMillis() / 1000;
        return asset;
    }

    private Domain createDomain(String... params) {
        int length = params.length;
        Domain domain = new Domain();
        domain.name = length > 0 ? params[0] : "name";
        domain.owner = length > 1 ? params[1] : "owner";
        domain.signature = length > 2 ? params[2] : "signature";
        domain.timestamp = System.currentTimeMillis() / 1000;
        return domain;
    }

    private Transaction createTransaction(String... params) {
        int length = params.length;
        Transaction transaction = new Transaction();
        transaction.assetUuid = length > 0 ? params[0] : "asset-uuid";
        transaction.assetName = length > 1 ? params[1] : "assetName";
        transaction.params = new Transaction.OperationParameter();
        transaction.params.command = length > 2 ? params[2] : "command";
        transaction.params.value = length > 3 ? params[3] : "value";
        transaction.params.sender = length > 4 ? params[4] : "sender";
        transaction.params.receiver = length > 5 ? params[5] : "receiver";
        transaction.params.timestamp = System.currentTimeMillis() / 1000;
        transaction.signature = length > 6 ? params[6] : "signature";
        return transaction;
    }

    private TransactionHistory createTransactionHistory(List<Transaction> tx) {
        TransactionHistory history = new TransactionHistory();
        history.history = tx;
        return history;
    }

    private Iroha buildIroha() {
        return irohaBuilder
                .baseUrl(mockWebServer.url("/").toString())
                .build();
    }

    private <T> void execute(Observable<T> observer, DisposableObserver<T> disposer) {
        observer.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(disposer);
    }
}
