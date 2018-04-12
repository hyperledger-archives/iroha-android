package jp.co.soramitsu.iroha.android.sample.main.send;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentSendBinding;

public class SendFragment extends Fragment implements SendView {
    private FragmentSendBinding binding;

    @Inject
    SendPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);

        RxView.clicks(binding.send)
                .subscribe(view -> presenter.sendTransaction(
                        binding.to.getText().toString().trim(),
                        binding.amount.getText().toString().trim()
                ));

        return binding.getRoot();
    }

    @Override
    public void didSendSuccess() {

    }

    @Override
    public void didSendError(Throwable error) {

    }
}
