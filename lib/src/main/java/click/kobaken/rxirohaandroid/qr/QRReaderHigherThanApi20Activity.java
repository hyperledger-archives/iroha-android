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

package click.kobaken.rxirohaandroid.qr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import click.kobaken.rxirohaandroid.exception.RuntimePermissionException;


public class QRReaderHigherThanApi20Activity extends QRReaderActivity {
    public static final String TAG = QRReaderHigherThanApi20Activity.class.getSimpleName();

    private CameraSource mCameraSource;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, QRReaderHigherThanApi20Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
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
        surfaceView.getHolder().removeCallback(this);
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
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new RuntimePermissionException());
                }
        }
    }

    @Override
    protected void initialize() {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        BarcodeProcessorFactory barcodeProcessorFactory = new BarcodeProcessorFactory();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeProcessorFactory).build());
        CameraSource.Builder cameraSourceBuilder = new CameraSource.Builder(this, barcodeDetector);
        cameraSourceBuilder.setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(30f)
                .setAutoFocusEnabled(true);
        mCameraSource = cameraSourceBuilder.build();
    }

    @Override
    protected void onSurfaceCreated(SurfaceHolder holder) {
        try {
            openCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // nothing
    }

    @Override
    protected void onSurfaceDestroyed(SurfaceHolder holder) {
        // nothing
    }

    private void openCamera() throws SecurityException, IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                return;
            }
        }

        mCameraSource.start(surfaceView.getHolder());
    }

    private class BarcodeTracker extends Tracker<Barcode> {
        @Override
        public void onNewItem(final int id, final Barcode item) {
            runOnUiThread(() -> {
                Log.d(TAG, "onNewItem: " + item.rawValue);

                finish();

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
