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

package click.kobaken.rxirohaandroid.di;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Date;

import javax.inject.Singleton;

import click.kobaken.rxirohaandroid.Iroha;
import click.kobaken.rxirohaandroid.repository.AccountRepository;
import click.kobaken.rxirohaandroid.repository.AssetRepository;
import click.kobaken.rxirohaandroid.repository.DomainRepository;
import click.kobaken.rxirohaandroid.repository.TransactionRepository;
import click.kobaken.rxirohaandroid.usecase.CreateAssetUseCase;
import click.kobaken.rxirohaandroid.usecase.GetAccountUseCase;
import click.kobaken.rxirohaandroid.usecase.GetAssetsUseCase;
import click.kobaken.rxirohaandroid.usecase.GetDomainsUseCase;
import click.kobaken.rxirohaandroid.usecase.GetTransactionUseCase;
import click.kobaken.rxirohaandroid.usecase.OperateAssetUseCase;
import click.kobaken.rxirohaandroid.usecase.RegisterAccountUseCase;
import click.kobaken.rxirohaandroid.usecase.RegisterDomainUseCase;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class IrohaModule {
    private Iroha.Builder builder;

    public IrohaModule(Iroha.Builder builder) {
        this.builder = builder;
    }

    @Singleton
    @Provides
    public AccountRepository provideAccountRepository() {
        return new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(AccountRepository.class);
    }

    @Singleton
    @Provides
    public DomainRepository provideDomainRepository() {
        return new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(DomainRepository.class);
    }

    @Singleton
    @Provides
    public AssetRepository provideAssetRepository() {
        return new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(AssetRepository.class);
    }

    @Singleton
    @Provides
    public TransactionRepository provideTransactionRepository() {
        return new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(TransactionRepository.class);
    }

    @Singleton
    @Provides
    public RegisterAccountUseCase provideRegisterAccountUseCase(AccountRepository accountRepository) {
        return new RegisterAccountUseCase(accountRepository);
    }

    @Singleton
    @Provides
    public GetAccountUseCase provideGetAccountUseCase(AccountRepository accountRepository) {
        return new GetAccountUseCase(accountRepository);
    }

    @Singleton
    @Provides
    public RegisterDomainUseCase provideRegisterDomainUseCase(DomainRepository domainRepository) {
        return new RegisterDomainUseCase(domainRepository);
    }

    @Singleton
    @Provides
    public GetDomainsUseCase provideGetDomainsUseCase(DomainRepository domainRepository) {
        return new GetDomainsUseCase(domainRepository);
    }

    @Singleton
    @Provides
    public CreateAssetUseCase provideCreateAssetUseCase(AssetRepository assetRepository) {
        return new CreateAssetUseCase(assetRepository);
    }

    @Singleton
    @Provides
    public GetAssetsUseCase provideGetAssetsUseCase(AssetRepository assetRepository) {
        return new GetAssetsUseCase(assetRepository);
    }

    @Singleton
    @Provides
    public OperateAssetUseCase provideOperateAssetUseCase(AssetRepository assetRepository) {
        return new OperateAssetUseCase(assetRepository);
    }

    @Singleton
    @Provides
    public GetTransactionUseCase provideGetTransactionUseCase(TransactionRepository transactionRepository) {
        return new GetTransactionUseCase(transactionRepository);
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
    }
}
