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
import click.kobaken.rxirohaandroid.service.AccountService;
import click.kobaken.rxirohaandroid.service.AssetService;
import click.kobaken.rxirohaandroid.service.DomainService;
import click.kobaken.rxirohaandroid.service.TransactionService;
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
    public AccountService provideAccountService() {
        return new AccountService(new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(AccountRepository.class));
    }

    @Singleton
    @Provides
    public DomainService provideDomainService() {
        return new DomainService(new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(DomainRepository.class));
    }

    @Singleton
    @Provides
    public AssetService provideAssetService() {
        return new AssetService(new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(AssetRepository.class));
    }

    @Singleton
    @Provides
    public TransactionService provideTransactionService() {
        return new TransactionService(new Retrofit.Builder()
                .baseUrl(builder.baseUrl)
                .client(builder.client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(TransactionRepository.class));
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
    }
}
