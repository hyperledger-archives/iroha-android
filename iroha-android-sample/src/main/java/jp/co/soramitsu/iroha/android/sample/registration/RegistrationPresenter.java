package jp.co.soramitsu.iroha.android.sample.registration;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.interactor.CreateAccountInteractor;
import lombok.Setter;

public class RegistrationPresenter {

    @Setter
    private RegistrationView view;

    private final CreateAccountInteractor createAccountInteractor;

    @Inject
    public RegistrationPresenter(CreateAccountInteractor createAccountInteractor) {
        this.createAccountInteractor = createAccountInteractor;
    }

    public void createAccount(String username, String details) {
        if (username.length() > 6) {
            createAccountInteractor.execute(() -> view.didRegistrationSuccess(), error -> view.didRegistrationError(error), username, details);
        } else {
            view.didRegistrationError(new Throwable("Username should be at least 6 symbols"));
        }
    }

}
