package io.soramitsu.irohaandroid.repository.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.AssetEntity;
import io.soramitsu.irohaandroid.entity.AssetListEntity;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.net.IrohaHttpClient;
import io.soramitsu.irohaandroid.net.Routes;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.repository.AssetRepository;
import okhttp3.Request;
import okhttp3.Response;

import static io.soramitsu.irohaandroid.net.IrohaHttpClient.createRequest;

public class AssetRepositoryImpl implements AssetRepository {

    private IrohaHttpClient httpClient = IrohaHttpClient.getInstance();
    private Gson gson = new Gson();

    @Override
    public AssetEntity create(AssetRegisterRequest body) throws IOException, HttpBadRequestException {
        String json = gson.toJson(body, AssetRegisterRequest.class);
        Request request = createRequest(Routes.ASSET_CREATE, json);
        Response response = httpClient.call(request);

        switch (response.code()) {
            case 200:
            case 201:
                return gson.fromJson(response.body().string(), new TypeToken<AssetEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }

    @Override
    public AssetListEntity findAssets(String domain, int limit, int offset)
            throws IOException, HttpBadRequestException {
        Request request = createRequest("/" + domain + Routes.ASSET_LIST + "?limit=" + limit + "&offset=" + offset);
        Response response = httpClient.call(request);

        switch (response.code()) {
            case 200:
                return gson.fromJson(response.body().string(), new TypeToken<AssetListEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }

    @Override
    public boolean operation(AssetOperationRequest body) throws IOException, HttpBadRequestException  {
        String json = gson.toJson(body, AssetOperationRequest.class);
        Request request = createRequest(Routes.ASSET_OPERATION, json);
        Response response = httpClient.call(request);

        switch (response.code()) {
            case 200:
            case 201:
                return true;
            default:
                throw new HttpBadRequestException();
        }
    }
}
