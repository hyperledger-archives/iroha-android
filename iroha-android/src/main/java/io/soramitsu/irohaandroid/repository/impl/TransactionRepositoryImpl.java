package io.soramitsu.irohaandroid.repository.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.net.IrohaHttpClient;
import io.soramitsu.irohaandroid.net.Routes;
import io.soramitsu.irohaandroid.repository.TransactionRepository;
import okhttp3.Request;
import okhttp3.Response;

import static io.soramitsu.irohaandroid.net.IrohaHttpClient.createRequest;

public class TransactionRepositoryImpl implements TransactionRepository {
    public static final String TAG = TransactionRepositoryImpl.class.getSimpleName();

    private IrohaHttpClient httpClient = IrohaHttpClient.getInstance();
    private Gson gson = new Gson();

    @Override
    public TransactionListEntity findHistory(String uuid, int limit, int offset)
            throws IOException, HttpBadRequestException {

        return findHistory(
                createRequest(
                        Routes.TRANSACTION_HISTORY_WITH_UUID
                                + "?uuid=" + uuid
                                + "&limit=" + limit
                                + "&offset=" + offset
                )
        );
    }

    @Override
    public TransactionListEntity findHistory(String uuid, String domain, String asset, int limit, int offset)
            throws IOException, HttpBadRequestException {

        return findHistory(
                createRequest(
                        Routes.TRANSACTION_HISTORY(domain, asset)
                                + "?uuid=" + uuid
                                + "&limit=" + limit
                                + "&offset=" + offset
                )
        );
    }

    private TransactionListEntity findHistory(Request request)
            throws IOException, HttpBadRequestException {
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "find history: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
                return gson.fromJson(responseBody, new TypeToken<TransactionListEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }
}
