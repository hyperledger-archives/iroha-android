package jp.co.soramitsu.iroha.android.sample.registration;

/**
 * Created by mrzizik on 4/3/18.
 */

public interface RegistrationView {

    void didRegistrationSuccess();

    void didRegistrationError(Throwable error);

    void showProgressDialog();

    void dissmissProgressDialog();

}
