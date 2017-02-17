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

import java.util.List;

import javax.inject.Inject;

import click.kobaken.rxirohaandroid.di.DaggerIrohaComponent;
import click.kobaken.rxirohaandroid.di.IrohaModule;
import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.Asset;
import click.kobaken.rxirohaandroid.model.BaseModel;
import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.model.TransactionHistory;
import click.kobaken.rxirohaandroid.usecase.CreateAssetUseCase;
import click.kobaken.rxirohaandroid.usecase.GetAccountUseCase;
import click.kobaken.rxirohaandroid.usecase.GetAssetsUseCase;
import click.kobaken.rxirohaandroid.usecase.GetDomainsUseCase;
import click.kobaken.rxirohaandroid.usecase.GetTransactionUseCase;
import click.kobaken.rxirohaandroid.usecase.OperateAssetUseCase;
import click.kobaken.rxirohaandroid.usecase.RegisterAccountUseCase;
import click.kobaken.rxirohaandroid.usecase.RegisterDomainUseCase;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;

public class Iroha {
    private static Iroha iroha;

    @Inject
    RegisterAccountUseCase registerAccountUseCase;

    @Inject
    GetAccountUseCase getAccountUseCase;

    @Inject
    RegisterDomainUseCase registerDomainUseCase;

    @Inject
    GetDomainsUseCase getDomainsUseCase;

    @Inject
    CreateAssetUseCase createAssetUseCase;

    @Inject
    GetAssetsUseCase getAssetsUseCase;

    @Inject
    OperateAssetUseCase operateAssetUseCase;

    @Inject
    GetTransactionUseCase getTransactionUseCase;

    private Iroha(Builder builder) {
        iroha = this;

        // UnitTest時に、自動生成されたDaggerTestComponentへのReferenceが解決出来ないため
        // Test以外の場合のみInjectするようにしてある
        if (!builder.isUnitTest) {
            DaggerIrohaComponent.builder()
                    .irohaModule(new IrohaModule(builder))
                    .build()
                    .inject(this);
        }
    }

    public static class Builder {
        public String baseUrl;
        public OkHttpClient client;
        public boolean isUnitTest; // true when running unit test

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder test(boolean isTest) {
            this.isUnitTest = isTest;
            return this;
        }

        public Iroha build() {
            if (baseUrl == null || client == null) {
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

    public Observable<Account> registerAccount(final String publicKey, final String alias) {
        return registerAccountUseCase.run(publicKey, alias);
    }

    public Observable<Account> findAccount(final String uuid) {
        return getAccountUseCase.run(uuid);
    }

    public Observable<Domain> registerDomain(
            final String name, final String owner, final String signature) {

        return registerDomainUseCase.run(name, owner, signature);
    }

    public Observable<List<Domain>> findDomains(final int limit, final int offset) {
        return getDomainsUseCase.run(limit, offset);
    }

    public Observable<Asset> createAsset(
            final String name, final String domain, final String creator,
            final String signature, final long timestamp) {

        return createAssetUseCase.run(name, domain, creator, signature, timestamp);
    }

    public Observable<List<Asset>> findAssets(
            final String domain, final int limit, final int offset) {

        return getAssetsUseCase.run(domain, limit, offset);
    }

    public Observable<BaseModel> operateAsset(
            final String assetUuid, final String command, final String value,
            final String sender, final String receiver, final String signature,
            final long timestamp) {

        return operateAssetUseCase.run(assetUuid, command, value, sender, receiver, signature, timestamp);
    }

    public Observable<TransactionHistory> findTransactionHistory(
            final String uuid, final int limit, final int offset) {

        return getTransactionUseCase.run(uuid, limit, offset);
    }

    public Observable<TransactionHistory> findTransactionHistory(
            final String domain, final String asset, final String uuid,
            final int limit, final int offset) {

        return getTransactionUseCase.run(domain, asset, uuid, limit, offset);
    }

    /* ============ 【Web API】 to here ============  */

}
