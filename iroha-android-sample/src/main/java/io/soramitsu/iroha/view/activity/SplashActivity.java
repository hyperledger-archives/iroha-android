/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.soramitsu.iroha.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.model.Account;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.view.fragment.SplashFragment;

import static io.soramitsu.iroha.IrohaApplication.isRegistered;

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

        final Context context = getApplicationContext();
        new Handler().postDelayed(() -> {
            if (isRegistered(context)) {
                final String uuid = Account.getUuid(context);
                navigator.navigateToMainActivity(context, uuid);
            } else {
                navigator.navigateToRegisterActivity(context);
            }
            finish();
            splashFragment.dismiss();
        }, 1000);
    }
}
