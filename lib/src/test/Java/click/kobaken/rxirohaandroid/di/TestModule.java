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

import javax.inject.Singleton;

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

import static org.mockito.Mockito.mock;

@Module
public class TestModule {

    @Singleton
    @Provides
    public AccountRepository provideAccountRepository() {
        return mock(AccountRepository.class);
    }

    @Singleton
    @Provides
    public DomainRepository provideDomainRepository() {
        return mock(DomainRepository.class);
    }

    @Singleton
    @Provides
    public AssetRepository provideAssetRepository() {
        return mock(AssetRepository.class);
    }

    @Singleton
    @Provides
    public TransactionRepository provideTransactionRepository() {
        return mock(TransactionRepository.class);
    }

    @Singleton
    @Provides
    public RegisterAccountUseCase provideRegisterAccountUseCase(AccountRepository accountRepository) {
        return mock(RegisterAccountUseCase.class);
    }

    @Singleton
    @Provides
    public GetAccountUseCase provideGetAccountUseCase(AccountRepository accountRepository) {
        return mock(GetAccountUseCase.class);
    }

    @Singleton
    @Provides
    public RegisterDomainUseCase provideRegisterDomainUseCase(DomainRepository domainRepository) {
        return mock(RegisterDomainUseCase.class);
    }

    @Singleton
    @Provides
    public GetDomainsUseCase provideGetDomainsUseCase(DomainRepository domainRepository) {
        return mock(GetDomainsUseCase.class);
    }

    @Singleton
    @Provides
    public CreateAssetUseCase provideCreateAssetUseCase(AssetRepository assetRepository) {
        return mock(CreateAssetUseCase.class);
    }

    @Singleton
    @Provides
    public GetAssetsUseCase provideGetAssetsUseCase(AssetRepository assetRepository) {
        return mock(GetAssetsUseCase.class);
    }

    @Singleton
    @Provides
    public OperateAssetUseCase provideOperateAssetUseCase(AssetRepository assetRepository) {
        return mock(OperateAssetUseCase.class);
    }

    @Singleton
    @Provides
    public GetTransactionUseCase provideGetTransactionUseCase(TransactionRepository transactionRepository) {
        return mock(GetTransactionUseCase.class);
    }
}
