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

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.model.Account;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.view.fragment.AccountRegisterFragment;

import static io.soramitsu.iroha.IrohaApplication.applyRegistered;

public class AccountRegisterActivity extends AppCompatActivity
        implements AccountRegisterFragment.AccountRegisterListener {
    public static final String TAG = AccountRegisterActivity.class.getSimpleName();

    private Navigator navigator = Navigator.getInstance();

    private io.soramitsu.iroha.databinding.ActivityAccountRegisterBinding binding;
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
        applyRegistered(context, true);
        navigator.navigateToMainActivity(context, Account.getUuid(context));
        finish();
    }
}
