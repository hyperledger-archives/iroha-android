package io.soramitsu.iroha.view;

public interface AccountRegisterView extends LoadingView {
    void showError(String error);

    void registerSuccessful();

    String getAlias();
}
