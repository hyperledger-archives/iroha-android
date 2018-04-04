package jp.co.soramitsu.iroha.android.sample.registration;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.logging.Logger;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.MainActivity;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity implements RegistrationView {

    private ActivityRegistrationBinding binding;

    @Inject
    RegistrationPresenter registrationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        SampleApplication.instance.getApplicationComponent().inject(this);

        registrationPresenter.setView(this);

        RxView.clicks(binding.registerButton)
                .subscribe(view -> registrationPresenter.createAccount(binding.username.getText().toString().trim()));
    }

    @Override
    public void didRegistrationSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, binding.logoImage, "profile");
        startActivity(intent, options.toBundle());
    }

    @Override
    public void didRegistrationError(Throwable error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
