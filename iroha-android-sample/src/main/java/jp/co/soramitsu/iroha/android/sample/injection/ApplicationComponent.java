package jp.co.soramitsu.iroha.android.sample.injection;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import jp.co.soramitsu.iroha.android.sample.main.MainActivity;
import jp.co.soramitsu.iroha.android.sample.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.main.receive.ReceiveFragment;
import jp.co.soramitsu.iroha.android.sample.main.send.SendFragment;
import jp.co.soramitsu.iroha.android.sample.registration.RegistrationActivity;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends AndroidInjector {

    void inject(HistoryFragment historyFragment);

    void inject(SendFragment sendFragment);

    void inject(ReceiveFragment receiveFragment);

    void inject(RegistrationActivity registrationActivity);

    void inject(MainActivity mainActivity);
}