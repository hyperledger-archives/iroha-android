package jp.co.soramitsu.iroha.android.sample.registration;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.interactor.CreateAccountInteractor;
import lombok.Setter;

public class RegistrationPresenter {

    private final PreferencesUtil preferencesUtil;
    @Setter
    private RegistrationView view;

    private final CreateAccountInteractor createAccountInteractor;

    @Inject
    public RegistrationPresenter(CreateAccountInteractor createAccountInteractor, PreferencesUtil preferencesUtil) {
        this.createAccountInteractor = createAccountInteractor;
        this.preferencesUtil = preferencesUtil;
    }

    public void createAccount(String username) {
        if (!username.isEmpty()) {
            createAccountInteractor.execute(username, () -> view.didRegistrationSuccess(), error -> view.didRegistrationError(error));
        } else {
            view.didRegistrationError(new Throwable("Username can't be empty"));
        }
    }

    public boolean isRegistered() {
        return !preferencesUtil.retrieveUsername().isEmpty();
    }

}
