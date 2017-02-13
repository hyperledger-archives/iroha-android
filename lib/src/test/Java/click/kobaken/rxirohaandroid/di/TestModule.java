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

import click.kobaken.rxirohaandroid.service.AccountService;
import click.kobaken.rxirohaandroid.service.AssetService;
import click.kobaken.rxirohaandroid.service.DomainService;
import click.kobaken.rxirohaandroid.service.TransactionService;
import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestModule {

    @Singleton
    @Provides
    public AccountService provideMockAccountService() {
        return mock(AccountService.class);
    }

    @Singleton
    @Provides
    public DomainService provideMockDomainService() {
        return mock(DomainService.class);
    }

    @Singleton
    @Provides
    public AssetService provideMockAssetService() {
        return mock(AssetService.class);
    }

    @Singleton
    @Provides
    public TransactionService provideMockTransactionService() {
        return mock(TransactionService.class);
    }
}
