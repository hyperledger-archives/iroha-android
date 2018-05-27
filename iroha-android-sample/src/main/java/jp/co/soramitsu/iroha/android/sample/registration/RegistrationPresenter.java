package jp.co.soramitsu.iroha.android.sample.registration;

import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.interactor.AddAssetInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.CreateAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountInteractor;
import lombok.Setter;

public class RegistrationPresenter {

    @Setter
    private RegistrationView view;
    private final PreferencesUtil preferencesUtil;

    private final CreateAccountInteractor createAccountInteractor;
    private final GetAccountInteractor getAccountInteractor;
    private final AddAssetInteractor addAssetInteractor;

    @VisibleForTesting
    public boolean isRequestFinished;

    @Inject
    public RegistrationPresenter(CreateAccountInteractor createAccountInteractor,
                                 GetAccountInteractor getAccountInteractor,
                                 AddAssetInteractor addAssetInteractor,
                                 PreferencesUtil preferencesUtil) {
        this.createAccountInteractor = createAccountInteractor;
        this.getAccountInteractor = getAccountInteractor;
        this.preferencesUtil = preferencesUtil;
        this.addAssetInteractor = addAssetInteractor;
    }

    void createAccount(String username) {
        isRequestFinished = false;

        if (!username.isEmpty()) {
            getAccountInteractor.execute(username, account -> {
                if (account.getAccountId().isEmpty()) {
                    createAccountInteractor.execute(username,
                            () -> addAssetInteractor.execute(username,
                                    () -> {
                                        isRequestFinished = true;
                                        view.didRegistrationSuccess();
                                    },
                                    this::didRegistrationError
                            ),
                            this::didRegistrationError);
                } else {
                    didRegistrationError(new Throwable(SampleApplication.instance
                            .getString(R.string.username_already_exists_error_dialog_message)));
                }
            }, this::didRegistrationError);
        } else {
            didRegistrationError(new Throwable(SampleApplication.instance.getString(R.string.username_empty_error_dialog_message)));
        }
    }

    private void didRegistrationError(Throwable throwable) {
        isRequestFinished = true;
        preferencesUtil.clear();
        view.didRegistrationError(throwable);
    }

    boolean isRegistered() {
        return !preferencesUtil.retrieveUsername().isEmpty();
    }

    void onStop() {
        view = null;
        createAccountInteractor.unsubscribe();
        getAccountInteractor.unsubscribe();
        addAssetInteractor.unsubscribe();
    }
}
