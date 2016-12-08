package io.soramitsu.irohaandroid.repository.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.AccountEntity;
import io.soramitsu.irohaandroid.exception.AccountDuplicateException;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.exception.UserNotFoundException;
import io.soramitsu.irohaandroid.net.Routes;
import io.soramitsu.irohaandroid.net.dataset.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.net.IrohaHttpClient;
import io.soramitsu.irohaandroid.repository.AccountRepository;
import okhttp3.Request;
import okhttp3.Response;

import static io.soramitsu.irohaandroid.net.IrohaHttpClient.createRequest;

public class AccountRepositoryImpl implements AccountRepository {

    private IrohaHttpClient httpClient = IrohaHttpClient.getInstance();
    private Gson gson = new Gson();

    @Override
    public AccountEntity register(AccountRegisterRequest body)
            throws IOException, AccountDuplicateException, HttpBadRequestException {
        String json = gson.toJson(body, AccountRegisterRequest.class);
        Request request = createRequest(Routes.ACCOUNT_REGISTER, json);
        Response response = httpClient.call(request);

        switch (response.code()) {
            case 200:
                return gson.fromJson(response.body().string(), new TypeToken<AccountEntity>(){}.getType());
            case 400:
                throw new AccountDuplicateException();
            default:
                throw new HttpBadRequestException();
        }
    }

    @Override
    public AccountEntity findByUuid(String uuid)
            throws IOException, UserNotFoundException, HttpBadRequestException {
        Request request = IrohaHttpClient.createRequest(Routes.ACCOUNT_INFO + uuid);
        Response response = httpClient.call(request);

        switch (response.code()) {
            case 200:
                return gson.fromJson(response.body().string(), new TypeToken<AccountEntity>(){}.getType());
            case 400:
                throw new UserNotFoundException();
            default:
                throw new HttpBadRequestException();
        }
    }
}
