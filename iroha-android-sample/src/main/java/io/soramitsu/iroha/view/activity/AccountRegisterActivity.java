package io.soramitsu.iroha.view.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.crypto.NoSuchPaddingException;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.ActivityAccountRegisterBinding;
import io.soramitsu.iroha.exception.ErrorMessageFactory;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.view.fragment.AccountRegisterFragment;
import io.soramitsu.irohaandroid.model.Account;

public class AccountRegisterActivity extends AppCompatActivity
        implements AccountRegisterFragment.AccountRegisterListener {
    public static final String TAG = AccountRegisterActivity.class.getSimpleName();

    private Navigator navigator = Navigator.getInstance();

    private ActivityAccountRegisterBinding binding;
    private InputMethodManager inputMethodManager;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, AccountRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_register);
        binding.backgroundImage.setAlpha(0.2f);
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.rotate);
        set.setTarget(binding.backgroundImage);
        set.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: ");
        inputMethodManager.hideSoftInputFromWindow(
                binding.getRoot().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
        );
        binding.getRoot().requestFocus();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onAccountRegisterSuccessful() {
        final Context context = getApplicationContext();
        final String uuid;
        try {
            uuid = Account.getUuid(context);
        } catch (NoSuchPaddingException | UnrecoverableKeyException | NoSuchAlgorithmException
                | KeyStoreException | InvalidKeyException | IOException e) {
            Toast.makeText(context, ErrorMessageFactory.create(context, e), Toast.LENGTH_SHORT).show();
            return;
        }
        navigator.navigateToMainActivity(context, uuid);
        finish();
    }
}
