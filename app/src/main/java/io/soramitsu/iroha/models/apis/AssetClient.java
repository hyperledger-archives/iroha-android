package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import java.util.List;

import io.soramitsu.iroha.models.Asset;


/**
 * Wrapper class of the Iroha asset transaction API.
 */
public class AssetClient extends TransactionClient<Asset> {
    private static AssetClient assetClient;

    private Gson gson;

    private AssetClient(Gson gson) {
        this.gson = gson;
    }

    /**
     * To generate new instance. (This is singleton)
     *
     * @return Instance of IrohaUserClient
     */
    public static AssetClient newInstance() {
        if (assetClient == null) {
            assetClient = new AssetClient(new Gson());
        }
        return assetClient;
    }

    @Override
    public Asset register() {
        return null;
    }

    @Override
    public List<Asset> list() {
        return null;
    }

    public void operation() {
    }
}
