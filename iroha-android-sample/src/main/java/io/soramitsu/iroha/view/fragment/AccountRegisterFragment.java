package io.soramitsu.iroha.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentAccountRegisterBinding;
import io.soramitsu.iroha.presenter.AccountRegisterPresenter;
import io.soramitsu.iroha.view.AccountRegisterView;
import io.soramitsu.iroha.view.dialog.ErrorDialog;
import io.soramitsu.iroha.view.dialog.ProgressDialog;
import io.soramitsu.iroha.view.dialog.SuccessDialog;

public class AccountRegisterFragment extends Fragment implements AccountRegisterView {
    public static final String TAG = AccountRegisterFragment.class.getSimpleName();

    private AccountRegisterPresenter accountRegisterPresenter = new AccountRegisterPresenter();

    private FragmentAccountRegisterBinding binding;
    private ErrorDialog errorDialog;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        errorDialog = new ErrorDialog(inflater);
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
    public void showError(String error) {
        errorDialog.show(getActivity(), error);
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
    public void showProgressDialog() {
        progressDialog.show(getActivity(), getString(R.string.during_registration));
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.hide();
    }
}
