package jp.co.soramitsu.iroha.android.sample.injection;

import android.support.v7.app.AppCompatActivity;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import jp.co.soramitsu.iroha.android.sample.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.registration.RegistrationActivity;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends AndroidInjector<AppCompatActivity> {

    void inject(HistoryFragment historyFragment);

    void inject(RegistrationActivity registrationActivity);
}