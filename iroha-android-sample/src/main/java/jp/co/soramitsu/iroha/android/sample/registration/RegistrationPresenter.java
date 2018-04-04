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
        createAccountInteractor.execute(() -> view.didRegistrationSuccess(), error -> view.didRegistrationError(error), username , details);
//        view.didRegistrationSuccess();
    }

}
