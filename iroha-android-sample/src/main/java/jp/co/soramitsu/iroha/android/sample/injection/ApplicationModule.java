package jp.co.soramitsu.iroha.android.sample.injection;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.co.soramitsu.iroha.android.sample.IrohaConnection;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;

@Module
public class ApplicationModule {
    public static final String JOB = "JOB";
    public static final String UI = "UI";

    @Provides
    @Singleton
    @Named(JOB)
    public Scheduler provideJobScheduler() {
        return Schedulers.computation();
    }

    @Provides
    @Singleton
    @Named(UI)
    public Scheduler provideUIScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    public IrohaConnection provideIrohaConnection() {
        return new IrohaConnection(SampleApplication.instance.getApplicationContext());
    }

}