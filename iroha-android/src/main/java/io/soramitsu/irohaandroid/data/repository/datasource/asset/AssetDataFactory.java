package io.soramitsu.irohaandroid.data.repository.datasource.asset;

import io.soramitsu.irohaandroid.data.net.RestApi;
import okhttp3.OkHttpClient;

public class AssetDataFactory {
    public AssetDataStore create() {
        return createCloudDataStore();
    }

    private AssetDataStore createCloudDataStore() {
        RestApi restApi = new RestApi(new OkHttpClient.Builder().build());
        return new CloudAssetDataStore(restApi);
    }
}
