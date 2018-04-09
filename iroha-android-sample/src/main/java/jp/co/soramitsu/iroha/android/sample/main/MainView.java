package jp.co.soramitsu.iroha.android.sample.main;

interface MainView {
    void setUsername(String username);

    void setAccountDetails(String details);

    void showRegistrationScreen();

    void showProgress();

    void hideProgress();

    void showSetDetailsAccountError();
}