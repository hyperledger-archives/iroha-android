package io.soramitsu.iroha.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.view.AccountRegisterView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.domain.entity.Account;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import rx.Subscriber;

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
        Iroha.getInstance().unsubscribeRegisterKeyPair();
        Iroha.getInstance().unsubscribeDeleteKeyPair();
        Iroha.getInstance().unsubscribeFetchKeyPair();
        Iroha.getInstance().unsubscribeRegisterAccount();
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

                final Iroha iroha = Iroha.getInstance();
                iroha.generateKeyPair(context, new Subscriber<Boolean>() {
                    AccountRegisterRequest body;
                    @Override
                    public void onCompleted() {
                        if (body == null) {
                            Log.d(TAG, "onCompleted: Cannot create keypair…");
                            accountRegisterView.showError(accountRegisterView.getContext().getString(R.string.error_message_retry_again));
                        } else {
                            registerAccount(body);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Boolean succeeded) {
                        if (succeeded) {
                            body = new AccountRegisterRequest();
                            body.alias = alias;
                            body.publicKey = iroha.findKeyPair(context).getPublicKey();
                        }
                    }
                });
            }
        };
    }

    private void registerAccount(AccountRegisterRequest body) {
        accountRegisterView.showProgressDialog();

        body.timestamp = System.currentTimeMillis() / 1000L;

        Iroha.getInstance().registerAccount(accountRegisterView.getContext(), body, new Subscriber<Account>() {
            String uuid;

            @Override
            public void onCompleted() {
                accountRegisterView.hideProgressDialog();
                accountRegisterView.registerSuccessful();
            }

            @Override
            public void onError(Throwable e) {
                accountRegisterView.hideProgressDialog();
//                Iroha.getInstance().removeKeyPair(accountRegisterView.getContext());

//                if (NetworkUtil.isOnline(accountRegisterView.getContext())) {
//                    accountRegisterView.showError(accountRegisterView.getContext().getString(R.string.error_message_retry_again));
//                } else {
//                    accountRegisterView.showError(accountRegisterView.getContext().getString(R.string.error_message_check_network_state));
//                }

                accountRegisterView.registerSuccessful();
            }

            @Override
            public void onNext(Account account) {
                uuid = account.uuid;
            }
        });
    }
}
