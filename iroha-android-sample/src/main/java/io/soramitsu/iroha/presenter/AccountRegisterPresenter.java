package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.view.AccountRegisterView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.KeyGenerator;
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
        // nothing
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
                            context.getString(R.string.validation_message_required, context.getString(R.string.name))
                    );
                    return;
                }

                accountRegisterView.showProgressDialog();

                KeyPair keyPair = KeyGenerator.createKeyPair();
                keyPair.save(context);
                register(keyPair, alias);
            }
        };
    }

    private void register(final KeyPair keyPair, final String alias) {
        Iroha.getInstance().registerAccount(keyPair.publicKey, alias, new Callback<Account>() {
            @Override
            public void onSuccessful(Account result) {
                result.save(accountRegisterView.getContext());

                accountRegisterView.hideProgressDialog();
                accountRegisterView.registerSuccessful();
            }

            @Override
            public void onFailure(Throwable throwable) {
                accountRegisterView.hideProgressDialog();

//                        keyPair.delete(accountRegisterView.getContext());

//                        if (NetworkUtil.isOnline(accountRegisterView.getContext())) {
//                            accountRegisterView.showError(accountRegisterView.getContext().getString(R.string.error_message_retry_again));
//                        } else {
//                            accountRegisterView.showError(accountRegisterView.getContext().getString(R.string.error_message_check_network_state));
//                        }

                accountRegisterView.registerSuccessful();
            }
        });
    }
}
