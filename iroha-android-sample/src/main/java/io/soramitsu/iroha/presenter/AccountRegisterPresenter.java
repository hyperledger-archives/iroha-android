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

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.exception.NetworkNotConnectedException;
import io.soramitsu.iroha.exception.RequiredArgumentException;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.AccountRegisterView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.security.KeyGenerator;
import io.soramitsu.irohaandroid.callback.Callback;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.model.KeyPair;

public class AccountRegisterPresenter implements Presenter<AccountRegisterView> {
    public static final String TAG = AccountRegisterPresenter.class.getSimpleName();

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
        Iroha.getInstance().cancelRegisterAccount();
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
        final Context context = accountRegisterView.getContext();
        Iroha.getInstance().registerAccount(keyPair.publicKey, alias, new Callback<Account>() {
            @Override
            public void onSuccessful(Account result) {
                accountRegisterView.hideProgress();

                try {
                    result.alias = accountRegisterView.getAlias();
                    result.save(accountRegisterView.getContext());
                } catch (InvalidKeyException | NoSuchAlgorithmException
                        | KeyStoreException | NoSuchPaddingException | IOException e) {
                    Log.e(TAG, "onSuccessful: ", e);
                    KeyPair.delete(accountRegisterView.getContext());
                    accountRegisterView.showError(ErrorMessageFactory.create(context, e));
                    return;
                }

                accountRegisterView.registerSuccessful();
            }

            @Override
            public void onFailure(Throwable throwable) {
                accountRegisterView.hideProgress();

                KeyPair.delete(accountRegisterView.getContext());

                if (NetworkUtil.isOnline(accountRegisterView.getContext())) {
                    accountRegisterView.showError(ErrorMessageFactory.create(context, throwable));
                } else {
                    accountRegisterView.showError(ErrorMessageFactory.create(context, new NetworkNotConnectedException()));
                }
            }
        });
    }
}
