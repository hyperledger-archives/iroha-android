package io.soramitsu.iroha.navigator;

import android.content.Context;

import io.soramitsu.iroha.view.activity.AccountRegisterActivity;
import io.soramitsu.iroha.view.activity.MainActivity;

public class Navigator {
    private static Navigator navigator;

    private Navigator() {
    }

    public static Navigator getInstance() {
        if (navigator == null) {
            navigator = new Navigator();
        }
        return navigator;
    }

    public void navigateToRegisterActivity(Context context) {
        if (context != null) {
            context.startActivity(AccountRegisterActivity.getCallingIntent(context));
        }
    }

    public void navigateToMainActivity(Context context, String uuid) {
        if (context != null) {
            context.startActivity(MainActivity.getCallingIntent(context, uuid));
        }
    }
}
