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

package io.soramitsu.iroha.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentAccountRegisterBinding;
import io.soramitsu.iroha.presenter.AccountRegisterPresenter;
import io.soramitsu.iroha.view.AccountRegisterView;
import io.soramitsu.irohaandroid.cache.FileManager;
import io.soramitsu.irohaandroid.security.KeyStoreManager;

public class AccountRegisterFragment extends Fragment implements AccountRegisterView {
    public static final String TAG = AccountRegisterFragment.class.getSimpleName();

    private AccountRegisterPresenter accountRegisterPresenter = new AccountRegisterPresenter();

    private FragmentAccountRegisterBinding binding;
    private SweetAlertDialog sweetAlertDialog;

    private AccountRegisterListener accountRegisterListener;

    public interface AccountRegisterListener {
        void onAccountRegisterSuccessful();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountRegisterPresenter.setView(this);
        accountRegisterPresenter.onCreate();

        FileManager fileManager = new FileManager();
        File extStorage = getContext().getExternalFilesDir("keypair");
        Log.d("Account soramitsu", "save: " + extStorage.toString());
        File uuidFile = new File(extStorage, "private_key.txt");
        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(getContext()).build();
        try {
            String result = keyStoreManager.decrypt(fileManager.readFileContent(uuidFile));
            Log.d(TAG, "onCreate: result: " + result);
        } catch (Exception e) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAccountRegisterBinding.bind(view);
        binding.userName.setOnKeyListener(accountRegisterPresenter.onKeyEventOnUserName());
        binding.registerButton.setOnClickListener(accountRegisterPresenter.onRegisterClicked());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!(getActivity() instanceof AccountRegisterListener)) {
            throw new ClassCastException();
        }
        accountRegisterListener = (AccountRegisterListener) getActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        accountRegisterPresenter.onStop();
    }

    @Override
    public void showError(final String error) {
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText(getString(R.string.error))
                .setContentText(error)
                .show();
    }

    @Override
    public void showWarning(final String warning) {
        binding.userNameContainer.setError(warning);
        binding.userNameContainer.setErrorEnabled(true);
    }

    @Override
    public void registerSuccessful() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.setTitleText(getString(R.string.successful))
                        .setContentText(getString(R.string.message_account_register_successful))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(finale SweetAlertDialog sweetAlertDialog) {
                                accountRegisterListener.onAccountRegisterSuccessful();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            }
        }, 1000);
    }

    @Override
    public String getAlias() {
        return binding.userName.getText().toString();
    }

    @Override
    public void showProgress() {
        binding.userNameContainer.setErrorEnabled(false);
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(getString(R.string.during_registration)).show();
    }

    @Override
    public void hideProgress() {
        // nothing
    }
}
