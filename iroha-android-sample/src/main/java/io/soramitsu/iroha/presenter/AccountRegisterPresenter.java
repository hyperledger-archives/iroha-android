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

package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import click.kobaken.rxirohaandroid.Iroha;
import click.kobaken.rxirohaandroid.callback.Callback;
import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.KeyPair;
import click.kobaken.rxirohaandroid.security.KeyGenerator;
import io.soramitsu.iroha.R;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.NetworkNotConnectedException;
import io.soramitsu.iroha.exception.RequiredArgumentException;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.AccountRegisterView;

public class AccountRegisterPresenter implements Presenter<AccountRegisterView> {
    public static final String TAG = AccountRegisterPresenter.class.getSimpleName();

    private static final String IROHA_TASK_TAG_ACCOUNT_REGISTER = "AccountRegister";

    private AccountRegisterView accountRegisterView;

    @Override
    public void setView(@NonNull AccountRegisterView view) {
        accountRegisterView = view;
    }

    @Override
    public void onCreate() {
        // nothing
    }

    @Override
    public void onStart() {
        // nothing
    }

    @Override
    public void onResume() {
        // nothing
    }

    @Override
    public void onPause() {
        // nothing
    }

    @Override
    public void onStop() {
        Iroha.getInstance().cancelAsyncTask(IROHA_TASK_TAG_ACCOUNT_REGISTER);
        accountRegisterView.hideProgress();
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public View.OnKeyListener onKeyEventOnUserName() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) accountRegisterView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        };
    }

    public View.OnClickListener onRegisterClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = accountRegisterView.getContext();
                final String alias = accountRegisterView.getAlias();

                if (alias.isEmpty()) {
                    accountRegisterView.showError(
                            ErrorMessageFactory.create(context, new RequiredArgumentException(), context.getString(R.string.name))
                    );
                    return;
                }

                accountRegisterView.showProgress();

                KeyPair keyPair = KeyGenerator.createKeyPair();
                try {
                    keyPair.save(context);
                } catch (InvalidKeyException | NoSuchAlgorithmException | KeyStoreException
                        | NoSuchPaddingException | IOException e) {
                    Log.e(TAG, "onClick: ", e);
                }
                register(keyPair, alias);
            }
        };
    }

    private void register(final KeyPair keyPair, final String alias) {
        Log.d(TAG, "register: " + keyPair.publicKey);
        Iroha iroha = Iroha.getInstance();
        iroha.runAsyncTask(
                IROHA_TASK_TAG_ACCOUNT_REGISTER,
                iroha.registerAccountFunction(keyPair.publicKey, alias),
                callback()
        );
    }

    private Callback<Account> callback() {
        return new Callback<Account>() {
            @Override
            public void onSuccessful(Account result) {
                registerSuccessful(result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                registerFailure(throwable);
            }
        };
    }

    private void registerSuccessful(Account result) {
        accountRegisterView.hideProgress();

        try {
            result.alias = accountRegisterView.getAlias();
            result.save(accountRegisterView.getContext());
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | KeyStoreException | NoSuchPaddingException | IOException e) {
            Log.e(TAG, "onSuccessful: ", e);
            KeyPair.delete(accountRegisterView.getContext());
            accountRegisterView.showError(ErrorMessageFactory.create(accountRegisterView.getContext(), e));
            return;
        }

        accountRegisterView.registerSuccessful();
    }

    private void registerFailure(Throwable throwable) {
        accountRegisterView.hideProgress();

        KeyPair.delete(accountRegisterView.getContext());

        Context c = accountRegisterView.getContext();
        if (NetworkUtil.isOnline(accountRegisterView.getContext())) {
            accountRegisterView.showError(ErrorMessageFactory.create(c, throwable));
        } else {
            accountRegisterView.showError(ErrorMessageFactory.create(c, new NetworkNotConnectedException()));
        }
    }
}
