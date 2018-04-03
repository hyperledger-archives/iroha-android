package jp.co.soramitsu.iroha.android.sample.registration;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.logging.Logger;

import jp.co.soramitsu.iroha.android.sample.MainActivity;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

        RxView.clicks(binding.registerButton)
                .subscribe(view -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(this, binding.logoImage, "profile");
                    startActivity(intent, options.toBundle());
                });
    }
}
