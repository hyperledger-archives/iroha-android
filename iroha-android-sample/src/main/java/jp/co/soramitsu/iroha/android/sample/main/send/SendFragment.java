package jp.co.soramitsu.iroha.android.sample.main.send;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentSendBinding;
import jp.co.soramitsu.iroha.android.sample.main.MainActivity;

import static jp.co.soramitsu.iroha.android.sample.main.send.SendPresenter.REQUEST_CODE_QR_SCAN;

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
                .subscribe(view -> sendTransaction(binding.to.getText().toString().trim(), binding.amount.getText().toString().trim()));

        RxView.clicks(binding.qr)
                .subscribe(view -> {
                    Dexter.withActivity(getActivity())
                            .withPermissions(Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        presenter.doScanQr();
                                    } else {
                                        didSendError(new Throwable("Permissions weren't granted"));
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();
                });

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void didSendSuccess() {
        ((MainActivity) getActivity()).refreshData(false);
        binding.amount.setText("");
        binding.to.setText("");
        ((MainActivity) getActivity()).hideProgress();
        Toast.makeText(getActivity(), getString(R.string.transaction_successful), Toast.LENGTH_LONG).show();
    }

    private void sendTransaction(String username, String amount) {
        ((MainActivity) getActivity()).showProgress();
        presenter.sendTransaction(username, amount);
    }

    @Override
    public void didSendError(Throwable error) {
        ((MainActivity) getActivity()).hideProgress();
        ((MainActivity) getActivity()).showError(error);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_QR_SCAN) {
                if (data == null) {
                    didSendError(new Throwable("QR can't be decoded."));
                } else {
                    String result = data.getData().toString();
                    if (result.contains(",") && result.split(",").length == 2) {
                        binding.to.setText(result.split(",")[0]);
                        binding.amount.setText(result.split(",")[1]);
                    } else {
                        didSendError(new Throwable("QR can't be decoded."));
                    }
                }
            }
        }
    }
}