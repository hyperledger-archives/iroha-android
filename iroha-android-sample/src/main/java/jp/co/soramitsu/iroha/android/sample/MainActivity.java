package jp.co.soramitsu.iroha.android.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    static {
        try {
            System.loadLibrary("irohajava");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ActivityMainBinding binding;
    private IrohaConnection irohaConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        irohaConnection = new IrohaConnection(this);


        InitialValueObservable username = RxTextView.textChanges(binding.username);
        InitialValueObservable details = RxTextView.textChanges(binding.details);

        Observable.mergeArray(username, details).subscribe(s -> {
            if (binding.username.getText().length() >= 3
                    && binding.details.getText().length() > 1) {
                binding.sendDetails.setEnabled(true);
                binding.sendDetails.setAlpha(1);
            } else {
                binding.sendDetails.setEnabled(false);
                binding.sendDetails.setAlpha(0.5f);
            }
        });

        RxView.clicks(binding.sendDetails)
                .subscribe(b -> {
                    binding.input.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    irohaConnection.execute(binding.username.getText().toString(),
                            binding.details.getText().toString())
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.getUserDetails.setText("Success! Result is " + result);
                            }, throwable -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.getUserDetails.setText(throwable.getMessage());
                            });
                });
    }
}