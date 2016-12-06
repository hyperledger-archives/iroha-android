package io.soramitsu.iroha.view.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.util.List;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.DialogQrReaderBinding;

public class QRReaderApi19DialogFragment extends DialogFragment implements SurfaceHolder.Callback {
    private static final String TAG = QRReaderApi19DialogFragment.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 70;

    @SuppressWarnings("deprecation")
    private Camera camera;
    private Handler autoFocusHandler;

    private DialogQrReaderBinding binding;

    private boolean flg;

    public static QRReaderApi19DialogFragment newInstance() {
        QRReaderApi19DialogFragment fragment = new QRReaderApi19DialogFragment();
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

        return new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        camera = Camera.open();
        if (flg) {
            Log.d(TAG, "flg is true!");
            initialize();
            camera.startPreview();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        autoFocusHandler.removeCallbacksAndMessages(null);
        binding.surfaceViewQrReaderFragment.getHolder().removeCallback(this);
        camera.cancelAutoFocus();
        camera.stopPreview();
        camera.release();
        flg = true;
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    private void initialize() {
        autoFocusHandler = new Handler();
        autoFocusHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "autoFocus!");
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera1) {
                        camera1.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] bytes, Camera camera2) {
                                int previewWidth = camera.getParameters().getPreviewSize().width;
                                int previewHeight = camera.getParameters().getPreviewSize().height;

                                PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                                        bytes, previewWidth, previewHeight, 0, 0, previewWidth, previewHeight, false);
                                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                                Reader reader = new MultiFormatReader();
                                try {
                                    Result result = reader.decode(bitmap);
                                    String text = result.getText();

                                    dismiss();

                                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                                    if (fragment instanceof OnQRReaderListener) {
                                        OnQRReaderListener handler = (OnQRReaderListener) fragment;
                                        handler.setOnResult(text);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "onPreviewFrame: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                autoFocusHandler.postDelayed(this, 5000);
            }
        }, 1000);
        binding.surfaceViewQrReaderFragment.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");
        try {
            int cameraPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE
                );
                return;
            }

            setParameters();
            setDisplayOrientation();
            setSurfaceViewSize();

            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: ");
        camera.stopPreview();

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: ");
    }

    @SuppressWarnings("deprecation")
    private void setParameters() {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = sizes.get(0);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    @SuppressWarnings("deprecation")
    private void setDisplayOrientation() {
        int degree;

        switch (getActivity().getWindowManager().getDefaultDisplay()
                .getRotation()) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
            default:
                degree = 0;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        camera.setDisplayOrientation((info.orientation - degree + 360) % 360);
    }

    @SuppressWarnings("deprecation")
    private void setSurfaceViewSize() {
        float relativeWidth = binding.getRoot().getWidth();
        float relativeHeight = binding.getRoot().getHeight();
        float previewWidth;
        float previewHeight;

        Camera.Size size = camera.getParameters().getSupportedPreviewSizes().get(0);
        if (relativeHeight > relativeWidth) {
            previewHeight = size.width;
            previewWidth = size.height;
        } else {
            previewHeight = size.height;
            previewWidth = size.width;
        }

        int surfaceWidth;
        int surfaceHeight;
        if (previewHeight / relativeHeight > previewWidth / relativeWidth) {
            surfaceWidth = (int) (previewWidth * relativeHeight / previewHeight);
            surfaceHeight = (int) (relativeHeight);
        } else {
            surfaceWidth = (int) (relativeWidth);
            surfaceHeight = (int) (previewHeight * relativeWidth / previewWidth);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                surfaceWidth, surfaceHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        binding.surfaceViewQrReaderFragment.setLayoutParams(params);
    }
}