package jp.co.soramitsu.iroha.android.sample.registration;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.interactor.AddAssetInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.CreateAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountInteractor;
import lombok.Setter;

public class RegistrationPresenter {

    private final PreferencesUtil preferencesUtil;
    @Setter
    private RegistrationView view;

    private final CreateAccountInteractor createAccountInteractor;
    private final GetAccountInteractor getAccountInteractor;
    private final AddAssetInteractor addAssetInteractor;

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
        if (!username.isEmpty()) {
            getAccountInteractor.execute(username, account -> {
                if (account.getAccountId().isEmpty()) {
                    createAccountInteractor.execute(username,
                            () -> addAssetInteractor.execute(username,
                                    () -> view.didRegistrationSuccess(),
                                    error -> didRegistrationError(error)
                            ),
                            error -> didRegistrationError(error));
                } else {
                    didRegistrationError(new Throwable(SampleApplication.instance.getString(R.string.username_already_exists_error_dialog_message)));
                }
            }, e -> didRegistrationError(e));
        } else {
            didRegistrationError(new Throwable(SampleApplication.instance.getString(R.string.username_empty_error_dialog_message)));
        }
    }

    private void didRegistrationError(Throwable throwable) {
        preferencesUtil.clear();
        view.didRegistrationError(throwable);
    }

    boolean isRegistered() {
        return !preferencesUtil.retrieveUsername().isEmpty();
    }

}
