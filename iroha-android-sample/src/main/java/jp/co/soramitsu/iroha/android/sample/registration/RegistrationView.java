package jp.co.soramitsu.iroha.android.sample.registration;

public interface RegistrationView {

    void didRegistrationSuccess();

    void didRegistrationError(Throwable error);

    void showProgressDialog();

    void dismissProgressDialog();

}
