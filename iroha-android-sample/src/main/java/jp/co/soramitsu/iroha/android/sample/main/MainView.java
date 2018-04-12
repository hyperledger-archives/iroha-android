package jp.co.soramitsu.iroha.android.sample.main;

interface MainView {
    void setUsername(String username);

    void setAccountDetails(String details);

    void setAccountBalance(String balance);

    void showRegistrationScreen();

    void showProgress();

    void hideProgress();

    void showError(Throwable throwable);

    void hideRefresh();

    void refreshData(boolean animate);
}