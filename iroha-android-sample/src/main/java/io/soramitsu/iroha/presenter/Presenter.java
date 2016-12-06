package io.soramitsu.iroha.presenter;

import android.support.annotation.NonNull;

import io.soramitsu.iroha.view.View;

interface Presenter<T extends View> {
    void setView(@NonNull T view);

    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
