package jp.co.soramitsu.iroha.android.sample.injection;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.co.soramitsu.iroha.android.ModelCrypto;
import jp.co.soramitsu.iroha.android.sample.R;
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
    public ManagedChannel provideManagedChannel() {
        return ManagedChannelBuilder.forAddress(SampleApplication.instance.getApplicationContext().getString(R.string.iroha_url),
                SampleApplication.instance.getApplicationContext().getResources().getInteger(R.integer.iroha_port)).usePlaintext(true).build();
    }

    @Provides
    @Singleton
    public ModelCrypto provideModelCrypto() {
        return new ModelCrypto();
    }

}