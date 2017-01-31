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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import click.kobaken.rxirohaandroid.cache.FileManager;
import click.kobaken.rxirohaandroid.security.KeyStoreManager;
import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentAccountRegisterBinding;
import io.soramitsu.iroha.presenter.AccountRegisterPresenter;
import io.soramitsu.iroha.view.AccountRegisterView;
import io.soramitsu.iroha.view.dialog.ProgressDialog;
import io.soramitsu.iroha.view.dialog.SuccessDialog;

public class AccountRegisterFragment extends Fragment implements AccountRegisterView {
    public static final String TAG = AccountRegisterFragment.class.getSimpleName();

    private AccountRegisterPresenter accountRegisterPresenter = new AccountRegisterPresenter();

    private FragmentAccountRegisterBinding binding;
    private SuccessDialog successDialog;
    private ProgressDialog progressDialog;

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
        successDialog = new SuccessDialog(inflater);
        progressDialog = new ProgressDialog(inflater);
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
        binding.userNameContainer.setError(error);
        binding.userNameContainer.setErrorEnabled(true);
    }

    @Override
    public void registerSuccessful() {
        successDialog.show(
                getActivity(),
                getString(R.string.register),
                getString(R.string.message_account_register_successful),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        successDialog.hide();
                        accountRegisterListener.onAccountRegisterSuccessful();
                    }
                });
    }

    @Override
    public String getAlias() {
        return binding.userName.getText().toString();
    }

    @Override
    public void showProgress() {
        binding.userNameContainer.setErrorEnabled(false);
        progressDialog.show(getActivity(), getString(R.string.during_registration));
    }

    @Override
    public void hideProgress() {
        progressDialog.hide();
    }
}
