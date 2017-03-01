/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

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

package click.kobaken.rxirohaandroid.qr.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import click.kobaken.rxirohaandroid.exception.RuntimePermissionException;
import click.kobaken.rxirohaandroid.qr.ReadQRCallback;

import static click.kobaken.rxirohaandroid.qr.view.QRReaderActivity.PERMISSION_REQUEST_CODE;
import static click.kobaken.rxirohaandroid.security.KeyStoreManager.TAG;

public class QRReaderHelperHigherThanApi20 implements QRReaderHelper {

    private Activity activity;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    protected ReadQRCallback callback;

    private QRReaderHelperHigherThanApi20() {
    }

    public static QRReaderHelperHigherThanApi20 newInstance(@NonNull Activity activity,
                                                            @NonNull ReadQRCallback callback,
                                                            @NonNull SurfaceView surfaceView) {
        QRReaderHelperHigherThanApi20 helper = new QRReaderHelperHigherThanApi20();
        helper.activity = activity;
        helper.callback = callback;
        helper.surfaceView = surfaceView;
        return helper;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                return;
            }
        }

        cameraSource.start(surfaceView.getHolder());
    }

    @Override
    public void onCreate() {
        initialize();
    }

    @Override
    public void onStart() {
        // nothing
    }

    @Override
    public void onResume() {
        // nothing
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        cameraSource.stop();
    }

    @Override
    public void onStop() {
        // nothing
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        cameraSource.release();
        surfaceView.getHolder().removeCallback(this);
    }

    private void initialize() {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(activity).setBarcodeFormats(Barcode.QR_CODE).build();

        BarcodeProcessorFactory barcodeProcessorFactory = new BarcodeProcessorFactory();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeProcessorFactory).build());
        CameraSource.Builder cameraSourceBuilder = new CameraSource.Builder(activity, barcodeDetector);
        cameraSourceBuilder.setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(30f)
                .setAutoFocusEnabled(true);
        cameraSource = cameraSourceBuilder.build();

        surfaceView.getHolder().addCallback(this);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Log.d(TAG, "onRequestPermissionsResult: init!");
                        initialize();
                        openCamera();
                    } catch (IOException e) {
                        Log.d(TAG, "onRequestPermissionsResult: " + e.getMessage());
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new RuntimePermissionException());
                }
        }
    }

    private class BarcodeTracker extends Tracker<Barcode> {
        @Override
        public void onNewItem(final int id, final Barcode item) {
            activity.runOnUiThread(() -> {
                Log.d(TAG, "onNewItem: " + item.rawValue);

                activity.finish();

                callback.onSuccessful(item.rawValue);
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
