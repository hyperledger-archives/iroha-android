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

package click.kobaken.rxirohaandroid_sample.presenter;

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
import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.model.KeyPair;
import click.kobaken.rxirohaandroid_sample.R;
import click.kobaken.rxirohaandroid_sample.exception.ErrorMessageFactory;
import click.kobaken.rxirohaandroid_sample.exception.NetworkNotConnectedException;
import click.kobaken.rxirohaandroid_sample.exception.RequiredArgumentException;
import click.kobaken.rxirohaandroid_sample.util.NetworkUtil;
import click.kobaken.rxirohaandroid_sample.view.AccountRegisterView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AccountRegisterPresenter implements Presenter<AccountRegisterView> {
    public static final String TAG = AccountRegisterPresenter.class.getSimpleName();

    private AccountRegisterView accountRegisterView;
    private CompositeDisposable compositeDisposable;

    @Override
    public void setView(@NonNull AccountRegisterView view) {
        accountRegisterView = view;
    }

    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable();
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
        accountRegisterView.hideProgress();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
    }

    public View.OnKeyListener onKeyEventOnUserName() {
        return (view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) accountRegisterView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return true;
            }
            return false;
        };
    }

    public View.OnClickListener onRegisterClicked() {
        return view -> {
            final Context context = accountRegisterView.getContext();
            final String alias = accountRegisterView.getAlias();

            if (alias.isEmpty()) {
                accountRegisterView.showError(
                        ErrorMessageFactory.create(context, new RequiredArgumentException(), context.getString(R.string.name))
                );
                return;
            }

            accountRegisterView.showProgress();

            KeyPair keyPair = Iroha.createKeyPair();
            try {
                keyPair.save(context);
            } catch (InvalidKeyException | NoSuchAlgorithmException | KeyStoreException
                    | NoSuchPaddingException | IOException e) {
                Log.e(TAG, "onClick: ", e);
            }
            register(keyPair, alias);
        };
    }

    private void register(final KeyPair keyPair, final String alias) {
        Log.d(TAG, "register: " + keyPair.publicKey);
        Disposable disposable = Iroha.getInstance().registerAccount(keyPair.publicKey, alias)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Account>() {
                    @Override
                    public void onNext(Account result) {
                        registerSuccessful(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        registerFailure(e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
        compositeDisposable.add(disposable);
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
