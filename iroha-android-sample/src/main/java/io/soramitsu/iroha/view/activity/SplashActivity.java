package io.soramitsu.iroha.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.view.fragment.SplashFragment;
import io.soramitsu.irohaandroid.model.Account;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = SplashActivity.class.getSimpleName();

    private Navigator navigator = Navigator.getInstance();

    private SplashFragment splashFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        openSplashFragment();
    }

    private void openSplashFragment() {
        splashFragment = SplashFragment.newInstance();
        splashFragment.show(getSupportFragmentManager(), SplashFragment.TAG);
        try {
            final Context context = getApplicationContext();
            final String uuid = Account.getUuid(getApplicationContext());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (uuid == null || uuid.isEmpty()) {
                        navigator.navigateToRegisterActivity(context);
                    } else {
                        navigator.navigateToMainActivity(context, uuid);
                    }
                    finish();
                    splashFragment.dismiss();
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();

            finish();
            splashFragment.dismiss();
        }
    }
}
