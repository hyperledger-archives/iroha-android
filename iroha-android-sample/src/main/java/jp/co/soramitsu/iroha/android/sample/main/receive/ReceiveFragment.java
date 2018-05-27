package jp.co.soramitsu.iroha.android.sample.main.receive;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.widget.RxTextView;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentReceiveBinding;
import jp.co.soramitsu.iroha.android.sample.main.MainActivity;

public class ReceiveFragment extends Fragment implements ReceiveView {
    private FragmentReceiveBinding binding;

    @Inject
    ReceivePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receive, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);
        presenter.generateQR(binding.amount.getText().toString());
        RxTextView.textChangeEvents(binding.amount)
                .subscribe(text -> presenter.generateQR(text.text().toString()),
                        this::didError);
        return binding.getRoot();
    }

    @Override
    public void didGenerateSuccess(Bitmap bitmap) {
        binding.qrCodeImageView.setImageBitmap(bitmap);
    }

    @Override
    public void didError(Throwable error) {
        ((MainActivity) getActivity()).showError(error);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }
}
