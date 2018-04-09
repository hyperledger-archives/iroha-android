package jp.co.soramitsu.iroha.android.sample.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityMainBinding;
import jp.co.soramitsu.iroha.android.sample.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.main.receive.ReceiveFragment;
import jp.co.soramitsu.iroha.android.sample.main.send.SendFragment;

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

    public static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> titles = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}