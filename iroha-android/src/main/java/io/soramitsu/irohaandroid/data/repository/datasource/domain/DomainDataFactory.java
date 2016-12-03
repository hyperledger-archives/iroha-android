package io.soramitsu.irohaandroid.data.repository.datasource.domain;

import io.soramitsu.irohaandroid.data.net.RestApi;
import okhttp3.OkHttpClient;

public class DomainDataFactory {
    public DomainDataStore create() {
        return createCloudDataStore();
    }

    private DomainDataStore createCloudDataStore() {
        RestApi restApi = new RestApi(new OkHttpClient.Builder().build());
        return new CloudDomainDataStore(restApi);
    }
}
