package jp.co.soramitsu.iroha.android.sample.registration;

import com.orhanobut.logger.Logger;

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
                                    error -> view.didRegistrationError(error)
                            ),
                            error -> view.didRegistrationError(error));
                } else {
                    view.didRegistrationError(new Throwable(SampleApplication.instance.getString(R.string.username_already_exists_error_dialog_message)));
                }
            }, e -> view.didRegistrationError(e));
        } else {
            view.didRegistrationError(new Throwable(SampleApplication.instance.getString(R.string.username_empty_error_dialog_message)));
        }
    }

    boolean isRegistered() {
        return !preferencesUtil.retrieveUsername().isEmpty();
    }

}
