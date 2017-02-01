/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

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

package click.kobaken.rxirohaandroid_sample.view.activity;

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

import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid_sample.R;
import click.kobaken.rxirohaandroid_sample.databinding.ActivityAccountRegisterBinding;
import click.kobaken.rxirohaandroid_sample.exception.ErrorMessageFactory;
import click.kobaken.rxirohaandroid_sample.navigator.Navigator;
import click.kobaken.rxirohaandroid_sample.view.fragment.AccountRegisterFragment;

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
