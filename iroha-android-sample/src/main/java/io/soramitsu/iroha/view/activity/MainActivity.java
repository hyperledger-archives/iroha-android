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

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.ActivityMainBinding;
import io.soramitsu.iroha.navigator.Navigator;
import io.soramitsu.iroha.view.fragment.AssetReceiveFragment;
import io.soramitsu.iroha.view.fragment.AssetSenderFragment;
import io.soramitsu.iroha.view.fragment.WalletFragment;
import io.soramitsu.irohaandroid.model.Account;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String BUNDLE_MAIN_ACTIVITY_KEY_UUID = "UUID";

    private Navigator navigator = Navigator.getInstance();

    private ActivityMainBinding binding;
    private InputMethodManager inputMethodManager;

    private WalletFragment walletFragment;
    private AssetSenderFragment assetSenderFragment;
    private AssetReceiveFragment assetReceiveFragment;
    private LibsSupportFragment libsFragment;

    private String uuid;

    public interface MainActivityListener {
        void onNavigationItemClicked();
    }

    public interface OnKeyboardVisibilityListener {
        void onVisibilityChanged(boolean isVisible);
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
        uuid = intent.getStringExtra(BUNDLE_MAIN_ACTIVITY_KEY_UUID);

        if (TextUtils.isEmpty(uuid)) {
            Log.d(TAG, "uuid is empty!");
            navigator.navigateToRegisterActivity(getApplicationContext());
            finish();
            return;
        }

        init(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: ");
        inputMethodManager.hideSoftInputFromWindow(
                binding.container.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
        );
        binding.container.requestFocus();
        return super.dispatchTouchEvent(ev);
    }

    private void init(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initToolbar();
        initNavigationHeader();
        initNavigationView();
        initBottomNavigationView();
        initFragments(savedInstanceState);
//        setKeyboardListener(new OnKeyboardVisibilityListener() {
//            @Override
//            public void onVisibilityChanged(boolean isVisible) {
//                binding.bottomNavigation.setVisibility(isVisible ? View.GONE : View.VISIBLE);
//            }
//        });
    }

    private void initToolbar() {
        binding.toolbar.setTitle(getString(R.string.receive));
        binding.toolbar.setNavigationIcon(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu_white_24dp));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initNavigationHeader() {
        final Context context = getApplicationContext();
        final String alias = Account.getAlias(context);
        final String uuid = Account.getUuid(context);
        View headerView = binding.navigation.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.name)).setText(alias);
        ((TextView) headerView.findViewById(R.id.email)).setText(uuid);
        Log.d(TAG, "initNavigationHeader: " + uuid);
    }

    private void initNavigationView() {
        binding.navigation.getMenu().getItem(0).setChecked(true);
        binding.navigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        boolean isChecked = item.isChecked();
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                if (isChecked) break;
                                binding.bottomNavigation.setVisibility(View.VISIBLE);
                                binding.toolbar.setTitle(getString(R.string.receive));
                                switchFragment(assetReceiveFragment, AssetReceiveFragment.TAG);
                                allClearNavigationMenuChecked();
                                allClearBottomNavigationMenuChecked();
                                binding.navigation.getMenu().getItem(0).setChecked(true);
                                binding.bottomNavigation.getMenu().getItem(0).setChecked(true);
                                break;
                            case R.id.action_unregister:
                                SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Account Delete")
                                        .setContentText("Are you sure you want to delete your account info?")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog
                                                        .setTitleText("Deleting…")
                                                        .setContentText(null)
                                                        .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                                                Account.delete(getApplicationContext());

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sweetAlertDialog
                                                                .setTitleText(getString(R.string.successful))
                                                                .setContentText(getString(R.string.message_account_deleted))
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        navigator.navigateToRegisterActivity(getApplicationContext());
                                                                        finish();
                                                                    }
                                                                })
                                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                    }
                                                }, 1000);
                                            }
                                        });
                                dialog.setCancelable(true);
                                dialog.show();
                                break;
                            case R.id.action_oss:
                                if (isChecked) break;
                                binding.bottomNavigation.setVisibility(View.GONE);
                                binding.toolbar.setTitle(getString(R.string.open_source_license));
                                switchFragment(libsFragment, "libs");
                                allClearNavigationMenuChecked();
                                binding.navigation.getMenu().getItem(2).setChecked(true);
                                break;
                        }
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                }
        );
    }

    private void initBottomNavigationView() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        allClearNavigationMenuChecked();
                        if (!item.isChecked()) {
                            binding.navigation.getMenu().getItem(0).setChecked(true);
                            switch (item.getItemId()) {
                                case R.id.action_receipt:
                                    Log.d(TAG, "onNavigationItemSelected: Receiver");
                                    binding.toolbar.setTitle(getString(R.string.receive));
                                    switchFragment(assetReceiveFragment, AssetReceiveFragment.TAG);
                                    break;
                                case R.id.action_wallet:
                                    Log.d(TAG, "onNavigationItemSelected: Wallet");
                                    binding.toolbar.setTitle(getString(R.string.wallet));
                                    switchFragment(walletFragment, WalletFragment.TAG);
                                    break;
                                case R.id.action_sender:
                                    Log.d(TAG, "onNavigationItemSelected: Sender");
                                    binding.toolbar.setTitle(getString(R.string.send));
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
    }

    private void allClearNavigationMenuChecked() {
        Menu menu = binding.navigation.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
    }

    private void allClearBottomNavigationMenuChecked() {
        Menu menu = binding.bottomNavigation.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
    }

    private void initFragments(Bundle savedInstanceState) {
        final FragmentManager manager = getSupportFragmentManager();
        assetReceiveFragment = (AssetReceiveFragment) manager.findFragmentByTag(AssetReceiveFragment.TAG);
        walletFragment = (WalletFragment) manager.findFragmentByTag(WalletFragment.TAG);
        assetSenderFragment = (AssetSenderFragment) manager.findFragmentByTag(AssetSenderFragment.TAG);

        if (assetReceiveFragment == null) {
            assetReceiveFragment = AssetReceiveFragment.newInstance(uuid);
        }
        if (walletFragment == null) {
            walletFragment = WalletFragment.newInstance(uuid);
        }
        if (assetSenderFragment == null) {
            assetSenderFragment = AssetSenderFragment.newInstance();
        }
        if (libsFragment == null) {
            libsFragment = new LibsBuilder()
                    .withAboutIconShown(true)
                    .withAboutVersionShown(true)
                    .withAboutDescription(getString(R.string.library_description))
                    .withLibraries("iroha_android")
                    .supportFragment();
        }

        if (savedInstanceState == null) {
            switchFragment(assetReceiveFragment, AssetReceiveFragment.TAG);
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

    private void setKeyboardListener(final OnKeyboardVisibilityListener listener) {
        final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    private boolean wasOpened;
                    private final Rect r = new Rect();

                    @Override
                    public void onGlobalLayout() {
                        activityRootView.getWindowVisibleDisplayFrame(r);

                        // Compare root view height and layout height.
                        int heightDiff = activityRootView.getRootView().getHeight() - r.height();

                        boolean isOpen = heightDiff > 200;

                        if (isOpen == wasOpened) {
                            // Since display state of keyboard should not change, do nothing.
                            return;
                        }

                        wasOpened = isOpen;

                        listener.onVisibilityChanged(isOpen);
                    }
                });
    }
}
