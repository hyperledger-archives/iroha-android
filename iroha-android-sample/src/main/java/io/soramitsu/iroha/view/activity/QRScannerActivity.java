package io.soramitsu.iroha.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.ActivityQrScannerBinding;

public class QRScannerActivity extends AppCompatActivity
        implements DecoratedBarcodeView.TorchListener {
    public static final String TAG = QRScannerActivity.class.getSimpleName();

    private ActivityQrScannerBinding binding;
    private CaptureManager captureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_scanner);

        binding.barcodeScanner.setTorchListener(this);

        captureManager = new CaptureManager(this, binding.barcodeScanner);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        captureManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        captureManager.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return binding.barcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTorchOn() {
        // nothing
    }

    @Override
    public void onTorchOff() {
        // nothing
    }
}
