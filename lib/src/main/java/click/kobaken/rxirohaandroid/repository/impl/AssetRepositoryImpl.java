/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

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

package click.kobaken.rxirohaandroid.repository.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import click.kobaken.rxirohaandroid.entity.AssetEntity;
import click.kobaken.rxirohaandroid.entity.AssetListEntity;
import click.kobaken.rxirohaandroid.exception.HttpBadRequestException;
import click.kobaken.rxirohaandroid.net.IrohaHttpClient;
import click.kobaken.rxirohaandroid.net.Routes;
import click.kobaken.rxirohaandroid.net.dataset.reqest.AssetOperationRequest;
import click.kobaken.rxirohaandroid.net.dataset.reqest.AssetRegisterRequest;
import click.kobaken.rxirohaandroid.repository.AssetRepository;
import okhttp3.Request;
import okhttp3.Response;

import static click.kobaken.rxirohaandroid.net.IrohaHttpClient.createRequest;

public class AssetRepositoryImpl implements AssetRepository {
    public static final String TAG = AssetRepositoryImpl.class.getSimpleName();

    private IrohaHttpClient httpClient = IrohaHttpClient.getInstance();
    private Gson gson = new Gson();

    @Override
    public AssetEntity create(AssetRegisterRequest body) throws IOException, HttpBadRequestException {
        String json = gson.toJson(body, AssetRegisterRequest.class);
        Request request = createRequest(Routes.ASSET_CREATE, json);
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "create asset: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
            case 201:
                return gson.fromJson(responseBody, new TypeToken<AssetEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }

    @Override
    public AssetListEntity findAssets(String domain, int limit, int offset)
            throws IOException, HttpBadRequestException {
        Request request = createRequest("/" + domain + Routes.ASSET_LIST + "?limit=" + limit + "&offset=" + offset);
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "find assets: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
                return gson.fromJson(responseBody, new TypeToken<AssetListEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }

    @Override
    public boolean operation(AssetOperationRequest body) throws IOException, HttpBadRequestException  {
        String json = gson.toJson(body, AssetOperationRequest.class);
        Request request = createRequest(Routes.ASSET_OPERATION, json);
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "operation: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
            case 201:
                return true;
            default:
                throw new HttpBadRequestException();
        }
    }
}
