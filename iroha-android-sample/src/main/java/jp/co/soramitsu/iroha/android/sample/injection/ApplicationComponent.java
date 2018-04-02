package jp.co.soramitsu.iroha.android.sample.injection;

import android.support.v7.app.AppCompatActivity;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import jp.co.soramitsu.iroha.android.sample.history.HistoryFragment;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends AndroidInjector<AppCompatActivity> {

    void inject(HistoryFragment historyFragment);
}