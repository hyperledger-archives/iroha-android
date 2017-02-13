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

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.Asset;
import click.kobaken.rxirohaandroid.model.BaseModel;
import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.model.TransactionHistory;
import click.kobaken.rxirohaandroid.service.AccountService;
import click.kobaken.rxirohaandroid.service.AssetService;
import click.kobaken.rxirohaandroid.service.DomainService;
import click.kobaken.rxirohaandroid.service.TransactionService;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static click.kobaken.rxirohaandroid.util.DummyCreator.newAccount;
import static click.kobaken.rxirohaandroid.util.DummyCreator.newAsset;
import static click.kobaken.rxirohaandroid.util.DummyCreator.newAssets;
import static click.kobaken.rxirohaandroid.util.DummyCreator.newDomain;
import static click.kobaken.rxirohaandroid.util.DummyCreator.newDomains;
import static click.kobaken.rxirohaandroid.util.DummyCreator.newTransactionHistory;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class IrohaTest {

    private CountDownLatch latch = new CountDownLatch(1);
    private Exception actualException;
    private Account actualAccount;
    private Domain actualDomain;
    private List<Domain> actualDomains;
    private Asset actualAsset;
    private List<Asset> actualAssets;
    private TransactionHistory actualTx;

    Iroha iroha;

    AccountService accountService;

    DomainService domainService;

    AssetService assetService;

    TransactionService transactionService;

    @Mock
    OkHttpClient okHttpClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        iroha = new Iroha.Builder()
                .baseUrl("http://localhost")
                .client(okHttpClient)
                .test(true)
                .build();

        // DaggerTestComponentのReferenceが解決できないため無理やりmock化
        iroha.accountService = accountService = mock(AccountService.class);
        iroha.domainService = domainService = mock(DomainService.class);
        iroha.assetService = assetService = mock(AssetService.class);
        iroha.transactionService = transactionService = mock(TransactionService.class);
    }

    @After
    public void tearDown() throws Exception {
        actualException = null;
        actualAccount = null;
        actualDomain = null;
        actualDomains = null;
        actualAsset = null;
        actualAssets = null;
        actualTx = null;
    }

    @Test
    public void testRegisterAccount_Successful() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        Account account = newAccount(10);
        when(accountService.register(publicKey, alias)).thenReturn(Observable.just(account));

        execute(iroha.registerAccount(publicKey, alias),
                new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account value) {
                        actualAccount = value;
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

        assertNotNull(actualAccount);
        assertThat(actualAccount.uuid, is("10"));
        assertThat(actualAccount.alias, is("10"));

        verify(accountService).register(publicKey, alias);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testRegisterAccount_AccountDuplicated() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        Account account = newAccount(10);
        account.status = 400;
        account.message = "duplicate user";
        when(accountService.register(publicKey, alias)).thenReturn(Observable.just(account));

        execute(iroha.registerAccount(publicKey, alias),
                new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account value) {
                        actualAccount = value;
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

        assertNotNull(actualAccount);
        assertThat(actualAccount.status, is(400));
        assertThat(actualAccount.message, is("duplicate user"));

        verify(accountService).register(publicKey, alias);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testRegisterAccount_Failure() throws Exception {
        final String publicKey = "pubkey";
        final String alias = "alias";

        when(accountService.register(publicKey, alias)).thenReturn(mockErrorResponse(404));

        execute(iroha.registerAccount(publicKey, alias),
                new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualAccount);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(accountService).register(publicKey, alias);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testFindAccount_Successful() throws Exception {
        final String uuid = "uuid";

        Account account = newAccount(10);
        when(accountService.findAccount(uuid)).thenReturn(Observable.just(account));

        execute(iroha.findAccount(uuid),
                new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account value) {
                        actualAccount = value;
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

        assertNotNull(actualAccount);
        assertThat(actualAccount.uuid, is("10"));
        assertThat(actualAccount.alias, is("10"));
        assertThat(actualAccount.assets.size(), is(10));
        Observable.range(0, 10).subscribe(i -> {
            String expected = String.valueOf(i);
            assertThat(actualAccount.assets.get(i).uuid, is(expected));
            assertThat(actualAccount.assets.get(i).name, is(expected));
            assertThat(actualAccount.assets.get(i).domain, is(expected));
            assertThat(actualAccount.assets.get(i).creator, is(expected));
            assertThat(actualAccount.assets.get(i).signature, is(expected));
            assertThat(actualAccount.assets.get(i).value, is(expected));
            assertThat(actualAccount.assets.get(i).timestamp, is((long) i));
        });

        verify(accountService).findAccount(uuid);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testFindAccount_NotFound() throws Exception {
        final String uuid = "uuid";

        Account account = newAccount(10);
        account.status = 400;
        account.message = "User not found";
        when(accountService.findAccount(uuid)).thenReturn(Observable.just(account));

        execute(iroha.findAccount(uuid),
                new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account value) {
                        actualAccount = value;
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

        assertNotNull(actualAccount);
        assertThat(actualAccount.status, is(400));
        assertThat(actualAccount.message, is("User not found"));

        verify(accountService).findAccount(uuid);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testFindAccount_Failure() throws Exception {
        final String uuid = "uuid";

        when(accountService.findAccount(uuid)).thenReturn(mockErrorResponse(404));

        execute(iroha.findAccount(uuid),
                new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualAccount);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(accountService).findAccount(uuid);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testRegisterDomain_Successful() throws Exception {
        final String name = "name";
        final String owner = "owner";
        final String signature = "signature";

        Domain domain = newDomain(10);
        when(domainService.register(name, owner, signature)).thenReturn(Observable.just(domain));

        execute(iroha.registerDomain(name, owner, signature),
                new DisposableObserver<Domain>() {
                    @Override
                    public void onNext(Domain value) {
                        actualDomain = value;
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

        assertNotNull(actualDomain);
        assertThat(actualDomain.name, is("10"));
        assertThat(actualDomain.owner, is("10"));
        assertThat(actualDomain.signature, is("10"));

        verify(domainService).register(name, owner, signature);
        verifyNoMoreInteractions(domainService);
    }

    @Test
    public void testRegisterDomain_Failure() throws Exception {
        final String name = "name";
        final String owner = "owner";
        final String signature = "signature";

        when(domainService.register(name, owner, signature)).thenReturn(mockErrorResponse(404));

        execute(iroha.registerDomain(name, owner, signature),
                new DisposableObserver<Domain>() {
                    @Override
                    public void onNext(Domain value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualDomain);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(domainService).register(name, owner, signature);
        verifyNoMoreInteractions(domainService);
    }

    @Test
    public void testFindDomains_Successful() throws Exception {
        final int limit = 30;
        final int offset = 0;

        List<Domain> domains = newDomains(10);
        when(domainService.findDomains(limit, offset)).thenReturn(Observable.just(domains));

        execute(iroha.findDomains(limit, offset),
                new DisposableObserver<List<Domain>>() {
                    @Override
                    public void onNext(List<Domain> value) {
                        actualDomains = value;
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

        assertThat(actualDomains.size(), is(10));
        Observable.range(0, 10).subscribe(i -> {
            String expected = String.valueOf(i);
            assertThat(actualDomains.get(i).name, is(expected));
            assertThat(actualDomains.get(i).owner, is(expected));
            assertThat(actualDomains.get(i).signature, is(expected));
        });

        verify(domainService).findDomains(limit, offset);
        verifyNoMoreInteractions(domainService);
    }

    @Test
    public void testFindDomains_Failure() throws Exception {
        final int limit = 30;
        final int offset = 0;

        when(domainService.findDomains(limit, offset)).thenReturn(mockErrorResponse(404));

        execute(iroha.findDomains(limit, offset),
                new DisposableObserver<List<Domain>>() {
                    @Override
                    public void onNext(List<Domain> value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualDomains);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(domainService).findDomains(limit, offset);
        verifyNoMoreInteractions(domainService);
    }

    @Test
    public void testCreateAsset_Successful() throws Exception {
        final String name = "name";
        final String domain = "domain";
        final String creator = "creator";
        final String signature = "signature";
        final long timestamp = 10L;

        Asset asset = newAsset(10);
        when(assetService.create(name, domain, creator, signature, timestamp))
                .thenReturn(Observable.just(asset));

        execute(iroha.createAsset(name, domain, creator, signature, asset.timestamp),
                new DisposableObserver<Asset>() {
                    @Override
                    public void onNext(Asset value) {
                        actualAsset = value;
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

        assertNotNull(actualAsset);
        assertThat(actualAsset.uuid, is("10"));
        assertThat(actualAsset.name, is("10"));
        assertThat(actualAsset.domain, is("10"));
        assertThat(actualAsset.creator, is("10"));
        assertThat(actualAsset.signature, is("10"));
        assertThat(actualAsset.timestamp, is(10L));

        verify(assetService).create(name, domain, creator, signature, timestamp);
        verifyNoMoreInteractions(assetService);
    }

    @Test
    public void testCreateAsset_Failure() throws Exception {
        final String name = "name";
        final String domain = "domain";
        final String creator = "creator";
        final String signature = "signature";
        final long timestamp = 10L;

        when(assetService.create(name, domain, creator, signature, timestamp))
                .thenReturn(mockErrorResponse(404));

        execute(iroha.createAsset(name, domain, creator, signature, timestamp),
                new DisposableObserver<Asset>() {
                    @Override
                    public void onNext(Asset value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualAsset);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(assetService).create(name, domain, creator, signature, timestamp);
        verifyNoMoreInteractions(assetService);
    }

    @Test
    public void testFindAssets_Successful() throws Exception {
        final String domain = "domain";
        final int limit = 30;
        final int offset = 0;

        List<Asset> assets = newAssets(10);
        when(assetService.findAssets(domain, limit, offset)).thenReturn(Observable.just(assets));

        execute(iroha.findAssets(domain, limit, offset),
                new DisposableObserver<List<Asset>>() {
                    @Override
                    public void onNext(List<Asset> value) {
                        actualAssets = value;
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

        assertThat(actualAssets.size(), is(10));
        Observable.range(0, 10).subscribe(i -> {
            String expected = String.valueOf(i);
            assertThat(actualAssets.get(i).uuid, is(expected));
            assertThat(actualAssets.get(i).name, is(expected));
            assertThat(actualAssets.get(i).domain, is(expected));
            assertThat(actualAssets.get(i).signature, is(expected));
        });

        verify(assetService).findAssets(domain, limit, offset);
        verifyNoMoreInteractions(assetService);
    }

    @Test
    public void testFindAssets_Failure() throws Exception {
        final String domain = "domain";
        final int limit = 30;
        final int offset = 0;

        when(assetService.findAssets(domain, limit, offset)).thenReturn(mockErrorResponse(404));

        execute(iroha.findAssets(domain, limit, offset),
                new DisposableObserver<List<Asset>>() {
                    @Override
                    public void onNext(List<Asset> value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualAssets);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(assetService).findAssets(domain, limit, offset);
        verifyNoMoreInteractions(assetService);
    }

    @Test
    public void testOperateAsset_Successful() throws Exception {
        final String assetUuid = "asset-uuid";
        final String command = "command";
        final String value = "value";
        final String sender = "sender";
        final String receiver = "receiver";
        final String signature = "signature";
        final long timestamp = 10L;

        Asset asset = newAsset(10);
        asset.status = 201;
        asset.message = "Asset transfered successfully";
        when(assetService.operation(assetUuid, command, value, sender, receiver, signature, timestamp))
                .thenReturn(Observable.just(asset));

        execute(iroha.operateAsset(assetUuid, command, value, sender, receiver, signature, timestamp),
                new DisposableObserver<BaseModel>() {
                    @Override
                    public void onNext(BaseModel value) {
                        actualAsset = (Asset) value;
                    }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNotNull(actualAsset);
        assertThat(actualAsset.status, is(201));
        assertThat(actualAsset.message, is("Asset transfered successfully"));

        verify(assetService).operation(assetUuid, command, value, sender, receiver, signature, timestamp);
        verifyNoMoreInteractions(assetService);
    }

    @Test
    public void testOperateAsset_Failure() throws Exception {
        final String assetUuid = "asset-uuid";
        final String command = "command";
        final String value = "value";
        final String sender = "sender";
        final String receiver = "receiver";
        final String signature = "signature";
        final long timestamp = 10L;

        when(assetService.operation(assetUuid, command, value, sender, receiver, signature, timestamp))
                .thenReturn(mockErrorResponse(404));

        execute(iroha.operateAsset(assetUuid, command, value, sender, receiver, signature, timestamp),
                new DisposableObserver<BaseModel>() {
                    @Override
                    public void onNext(BaseModel value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualAsset);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(assetService).operation(assetUuid, command, value, sender, receiver, signature, timestamp);
        verifyNoMoreInteractions(assetService);
    }

    @Test
    public void testFindTransactionHistory_Successful() throws Exception {
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        TransactionHistory history = newTransactionHistory(10);
        when(transactionService.findHistory(uuid, limit, offset)).thenReturn(Observable.just(history));

        execute(iroha.findTransactionHistory(uuid, limit, offset),
                new DisposableObserver<TransactionHistory>() {
                    @Override
                    public void onNext(TransactionHistory value) {
                        actualTx = value;
                        assertThat(value.history.size(), is(10));
                        assertThat(value.history.get(0).assetUuid, is("0"));
                        assertThat(value.history.get(0).assetName, is("0"));
                        assertThat(value.history.get(0).signature, is("0"));
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

        assertNotNull(actualTx);
        assertThat(actualTx.history.size(), is(10));
        Observable.range(0, 10).subscribe(i -> {
            String expected = String.valueOf(i);
            assertThat(actualTx.history.get(i).assetUuid, is(expected));
            assertThat(actualTx.history.get(i).assetName, is(expected));
            assertThat(actualTx.history.get(i).signature, is(expected));
            assertThat(actualTx.history.get(i).params.command, is(expected));
            assertThat(actualTx.history.get(i).params.value, is(expected));
            assertThat(actualTx.history.get(i).params.sender, is(expected));
            assertThat(actualTx.history.get(i).params.receiver, is(expected));
        });

        verify(transactionService).findHistory(uuid, limit, offset);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testFindTransactionHistory_Failure() throws Exception {
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        when(transactionService.findHistory(uuid, limit, offset)).thenReturn(mockErrorResponse(404));

        execute(iroha.findTransactionHistory(uuid, limit, offset),
                new DisposableObserver<TransactionHistory>() {
                    @Override
                    public void onNext(TransactionHistory value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualTx);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(transactionService).findHistory(uuid, limit, offset);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testFindTransactionHistory_MultiAsset_Successful() throws Exception {
        final String domain = "domain";
        final String asset = "asset";
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        TransactionHistory history = newTransactionHistory(10);
        when(transactionService.findHistory(domain, asset, uuid, limit, offset))
                .thenReturn(Observable.just(history));

        execute(iroha.findTransactionHistory(domain, asset, uuid, limit, offset),
                new DisposableObserver<TransactionHistory>() {
                    @Override
                    public void onNext(TransactionHistory value) {
                        actualTx = value;
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

        assertNotNull(actualTx);
        assertThat(actualTx.history.size(), is(10));
        Observable.range(0, 10).subscribe(i -> {
            String expected = String.valueOf(i);
            assertThat(actualTx.history.get(i).assetUuid, is(expected));
            assertThat(actualTx.history.get(i).assetName, is(expected));
            assertThat(actualTx.history.get(i).signature, is(expected));
            assertThat(actualTx.history.get(i).params.command, is(expected));
            assertThat(actualTx.history.get(i).params.value, is(expected));
            assertThat(actualTx.history.get(i).params.sender, is(expected));
            assertThat(actualTx.history.get(i).params.receiver, is(expected));
        });

        verify(transactionService).findHistory(domain, asset, uuid, limit, offset);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testFindTransactionHistory_MultiAsset_Failure() throws Exception {
        final String domain = "domain";
        final String asset = "asset";
        final String uuid = "uuid";
        final int limit = 30;
        final int offset = 0;

        when(transactionService.findHistory(domain, asset, uuid, limit, offset))
                .thenReturn(mockErrorResponse(404));

        execute(iroha.findTransactionHistory(domain, asset, uuid, limit, offset),
                new DisposableObserver<TransactionHistory>() {
                    @Override
                    public void onNext(TransactionHistory value) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable e) {
                        actualException = (Exception) e;
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();

        assertNull(actualTx);
        assertNotNull(actualException);
        assertTrue(actualException instanceof HttpException);

        verify(transactionService).findHistory(domain, asset, uuid, limit, offset);
        verifyNoMoreInteractions(transactionService);
    }

    private <T> void execute(Observable<T> observer, DisposableObserver<T> disposer) {
        observer.subscribe(disposer);
    }

    private <T> Observable<T> mockErrorResponse(int code) {
        return Observable.create(e -> {
            ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), "");
            e.onError(new HttpException(Response.error(code, body)));
        });
    }
}
