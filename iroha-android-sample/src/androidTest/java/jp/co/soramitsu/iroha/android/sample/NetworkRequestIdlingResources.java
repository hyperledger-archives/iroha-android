package jp.co.soramitsu.iroha.android.sample;

import android.support.test.espresso.IdlingResource;

import jp.co.soramitsu.iroha.android.sample.registration.RegistrationPresenter;

public class NetworkRequestIdlingResources implements IdlingResource {

    private final RegistrationPresenter presenter;
    private ResourceCallback resourceCallback;


    public NetworkRequestIdlingResources(RegistrationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isIdleNow() {
        boolean isIdle = presenter.isRequestFinished;

        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }

        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        resourceCallback = callback;
    }
}