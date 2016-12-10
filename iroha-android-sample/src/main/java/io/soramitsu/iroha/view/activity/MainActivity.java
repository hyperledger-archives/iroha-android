package io.soramitsu.iroha.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.ActivityMainBinding;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.view.fragment.AssetReceiverFragment;
import io.soramitsu.iroha.view.fragment.AssetSenderFragment;
import io.soramitsu.iroha.view.fragment.TransactionHistoryFragment;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String BUNDLE_MAIN_ACTIVITY_KEY_UUID = "UUID";

    private Navigator navigator = Navigator.getInstance();

    private ActivityMainBinding binding;

    private TransactionHistoryFragment transactionHistoryFragment;
    private AssetSenderFragment assetSenderFragment;
    private AssetReceiverFragment assetReceiverFragment;

    public interface MainActivityListener {
        void onNavigationItemClicked();
    }

    public static Intent getCallingIntent(Context context, String uuid) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(BUNDLE_MAIN_ACTIVITY_KEY_UUID, uuid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String uuid = intent.getStringExtra(BUNDLE_MAIN_ACTIVITY_KEY_UUID);
        if (uuid == null || uuid.isEmpty()) {
            navigator.navigateToRegisterActivity(getApplicationContext());
            finish();
            return;
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.toolbar.setTitle(getString(R.string.transaction_history));
        binding.bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (!item.isChecked()) {
                            switch (item.getItemId()) {
                                case R.id.action_receipt:
                                    Log.d(TAG, "onNavigationItemSelected: 要求");
                                    switchFragment(assetReceiverFragment, AssetReceiverFragment.TAG);
                                    binding.toolbar.setTitle(getString(R.string.receive));
                                    break;
                                case R.id.action_transaction_history:
                                    Log.d(TAG, "onNavigationItemSelected: 取引履歴");
                                    binding.toolbar.setTitle(getString(R.string.transaction_history));
                                    switchFragment(transactionHistoryFragment, TransactionHistoryFragment.TAG);
                                    break;
                                case R.id.action_sender:
                                    Log.d(TAG, "onNavigationItemSelected: 送金");
                                    binding.toolbar.setTitle(getString(R.string.sender));
                                    switchFragment(assetSenderFragment, AssetSenderFragment.TAG);
                                    break;
                            }
                        } else {
                            Log.d(TAG, "onNavigationItemSelected: Topへ!");
                            final FragmentManager manager = getSupportFragmentManager();
                            MainActivityListener listener = (MainActivityListener) manager.findFragmentById(R.id.container);
                            listener.onNavigationItemClicked();
                        }
                        return true;
                    }
                });

        final FragmentManager manager = getSupportFragmentManager();
        assetReceiverFragment = (AssetReceiverFragment) manager.findFragmentByTag(AssetReceiverFragment.TAG);
        transactionHistoryFragment = (TransactionHistoryFragment) manager.findFragmentByTag(TransactionHistoryFragment.TAG);
        assetSenderFragment = (AssetSenderFragment) manager.findFragmentByTag(AssetSenderFragment.TAG);

        if (assetReceiverFragment == null) {
            assetReceiverFragment = AssetReceiverFragment.newInstance();
        }
        if (transactionHistoryFragment == null) {
            transactionHistoryFragment = TransactionHistoryFragment.newInstance();
        }
        if (assetSenderFragment == null) {
            assetSenderFragment = AssetSenderFragment.newInstance();
        }

        if (savedInstanceState == null) {
            switchFragment(assetReceiverFragment, AssetReceiverFragment.TAG);
        }
    }

    private void switchFragment(@NonNull Fragment fragment, String tag) {
        if (fragment.isAdded()) {
            return;
        }

        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = manager.beginTransaction();

        final Fragment currentFragment = manager.findFragmentById(R.id.container);
        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment);
        }

        if (fragment.isDetached()) {
            fragmentTransaction.attach(fragment);
        } else {
            fragmentTransaction.add(R.id.container, fragment, tag);
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
