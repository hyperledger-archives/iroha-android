package io.soramitsu.iroha.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.view.fragment.AccountRegisterFragment;
import io.soramitsu.irohaandroid.model.Account;

public class AccountRegisterActivity extends AppCompatActivity
        implements AccountRegisterFragment.AccountRegisterListener {

    private Navigator navigator = Navigator.getInstance();

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, AccountRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);
    }

    @Override
    public void onAccountRegisterSuccessful() {
        final Context context = getApplicationContext();
//        navigator.navigateToMainActivity(context, Account.getUuid(context));
        navigator.navigateToMainActivity(context, "test"); // TODO mock用
        finish();
    }
}
