package io.soramitsu.iroha.view.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.DialogQrReaderBinding;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class QRReaderDialogFragment extends DialogFragment implements SurfaceHolder.Callback {
    private static final String TAG = QRReaderDialogFragment.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 70;

    private CameraSource mCameraSource;

    private DialogQrReaderBinding binding;
    private Dialog dialog;

    public static QRReaderDialogFragment newInstance() {
        QRReaderDialogFragment fragment = new QRReaderDialogFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_qr_reader, null, false);

        if (binding.surfaceViewQrReaderFragment == null) {
            throw new IllegalStateException("View must have @id/surfaceViewQrReaderFragment");
        }

        initialize();

        dialog = new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .create();

        return dialog;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        mCameraSource.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mCameraSource.release();
        binding.surfaceViewQrReaderFragment.getHolder().removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Log.d(TAG, "onRequestPermissionsResult: init!");
                        initialize();
                        openCamera();
                    } catch (IOException e) {
                        Log.d(TAG, "onRequestPermissionsResult: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    DialogFragment fragment = ErrorDialog.newInstance("カメラが利用できない場合このアプリは使用できません");
                    fragment.show(getFragmentManager(), "test");
                }
        }
    }

    private void initialize() {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getActivity()).setBarcodeFormats(Barcode.QR_CODE).build();

        BarcodeProcessorFactory barcodeProcessorFactory = new BarcodeProcessorFactory();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeProcessorFactory).build());
        CameraSource.Builder cameraSourceBuilder = new CameraSource.Builder(getActivity(), barcodeDetector);
        cameraSourceBuilder.setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(10f)
                .setAutoFocusEnabled(true);
        mCameraSource = cameraSourceBuilder.build();

        binding.surfaceViewQrReaderFragment.getHolder().removeCallback(this);
        binding.surfaceViewQrReaderFragment.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            openCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // nothing
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // nothing
    }

    private void openCamera() throws SecurityException, IOException {
        if (checkSelfPermission(
                getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            return;
        }

        mCameraSource.start(binding.surfaceViewQrReaderFragment.getHolder());

        Display d = getActivity().getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        ViewGroup.LayoutParams layoutParams = binding.surfaceViewQrReaderFragment.getLayoutParams();
        layoutParams.width = (int) ((float) p.x / (float) mCameraSource.getPreviewSize().getHeight() * 690);
        layoutParams.height = (int) ((float) p.y / (float) mCameraSource.getPreviewSize().getWidth() * 660);
        binding.surfaceViewQrReaderFragment.setLayoutParams(layoutParams);
    }

    public static class ErrorDialog extends DialogFragment {
        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    })
                    .create();
        }
    }

    private class BarcodeTracker extends Tracker<Barcode> {

        @Override
        public void onNewItem(final int id, final Barcode item) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onNewItem: " + item.rawValue);

                    dismiss();

                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                    if (fragment instanceof OnQRReaderListener) {
                        OnQRReaderListener handler = (OnQRReaderListener) fragment;
                        handler.setOnResult(item.rawValue);
                    }
                }
            });
        }
    }

    private class BarcodeProcessorFactory implements MultiProcessor.Factory<Barcode> {
        @Override
        public Tracker<Barcode> create(Barcode barcode) {
            return new BarcodeTracker();
        }
    }
}