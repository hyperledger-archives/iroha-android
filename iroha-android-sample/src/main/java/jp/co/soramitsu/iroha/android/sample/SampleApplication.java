package jp.co.soramitsu.iroha.android.sample;

import android.app.Application;


public class SampleApplication extends Application {
    static {
        try {
            System.loadLibrary("irohajava");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
