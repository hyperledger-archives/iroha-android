package io.soramitsu.iroha.view;

public interface LoadingView extends View {
    void showProgress();

    void hideProgress();
}
