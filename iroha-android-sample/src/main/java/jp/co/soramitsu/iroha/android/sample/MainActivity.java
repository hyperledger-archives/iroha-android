package jp.co.soramitsu.iroha.android.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jp.co.soramitsu.iroha.android.sample.databinding.ActivityMainBinding;
import jp.co.soramitsu.iroha.android.sample.history.HistoryFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupViewPager();
        binding.tabs.setupWithViewPager(binding.content);
    }

    private void setupViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new SendFragment(), "SEND");
        adapter.addFragment(new ReceiveFragment(), "RECEIVE");
        adapter.addFragment(new HistoryFragment(), "HISTORY");
        binding.content.setAdapter(adapter);
    }
}