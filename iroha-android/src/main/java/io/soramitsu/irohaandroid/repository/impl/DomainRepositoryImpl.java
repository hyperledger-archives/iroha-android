package io.soramitsu.irohaandroid.repository.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.DomainEntity;
import io.soramitsu.irohaandroid.entity.DomainListEntity;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.net.IrohaHttpClient;
import io.soramitsu.irohaandroid.net.Routes;
import io.soramitsu.irohaandroid.net.dataset.reqest.DomainRegisterRequest;
import io.soramitsu.irohaandroid.repository.DomainRepository;
import okhttp3.Request;
import okhttp3.Response;

import static io.soramitsu.irohaandroid.net.IrohaHttpClient.createRequest;

public class DomainRepositoryImpl implements DomainRepository {
    public static final String TAG = DomainRepositoryImpl.class.getSimpleName();

    private IrohaHttpClient httpClient = IrohaHttpClient.getInstance();
    private Gson gson = new Gson();

    @Override
    public DomainEntity register(DomainRegisterRequest body)
            throws IOException, HttpBadRequestException {
        String json = gson.toJson(body, DomainRegisterRequest.class);
        Request request = createRequest(Routes.DOMAIN_REGISTER, json);
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "register domain: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
            case 201:
                return gson.fromJson(responseBody, new TypeToken<DomainEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }

    @Override
    public DomainListEntity findDomains(int limit, int offset)
            throws IOException, HttpBadRequestException {
        Request request = createRequest(Routes.DOMAIN_LIST + "?limit=" + limit + "&offset=" + offset);
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "find domains: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
                return gson.fromJson(responseBody, new TypeToken<DomainListEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }
}
