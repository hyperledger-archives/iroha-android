package jp.co.soramitsu.iroha.android.sample;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import jp.co.soramitsu.iroha.android.sample.data.Account;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationComponent;
import jp.co.soramitsu.iroha.android.sample.injection.DaggerApplicationComponent;
import lombok.Getter;

public class SampleApplication extends Application {
    static {
        try {
            System.loadLibrary("irohajava");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SampleApplication instance;

    public Account account;

    @Getter
    @VisibleForTesting
    public ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent.builder().build();
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}