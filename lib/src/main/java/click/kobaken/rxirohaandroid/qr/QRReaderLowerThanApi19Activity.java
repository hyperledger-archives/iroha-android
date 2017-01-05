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
import android.hardware.Camera;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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

public class QRReaderLowerThanApi19Activity extends QRReaderActivity {
    @SuppressWarnings("deprecation")
    private Camera camera;
    private Handler autoFocusHandler;

    private boolean flg;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, QRReaderLowerThanApi19Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onStart() {
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
        surfaceView.getHolder().removeCallback(this);
        camera.cancelAutoFocus();
        camera.stopPreview();
        camera.release();
        flg = true;
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    protected void initialize() {
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

                                    finish();

                                    callback.onSuccessful(text);
//                                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
//                                    if (fragment instanceof OnQRReaderListener) {
//                                        OnQRReaderListener handler = (OnQRReaderListener) fragment;
//                                        handler.setOnResult(text);
//                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "onPreviewFrame: " + e.getMessage());
                                    callback.onFailure(e);
                                }
                            }
                        });
                    }
                });
                autoFocusHandler.postDelayed(this, 5000);
            }
        }, 1000);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    protected void onSurfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");
        try {
            int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
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
    protected void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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
    protected void onSurfaceDestroyed(SurfaceHolder holder) {
        // nothing
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

        switch (getWindowManager().getDefaultDisplay()
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
        float relativeWidth = surfaceView.getWidth();
        float relativeHeight = surfaceView.getHeight();
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
        surfaceView.setLayoutParams(params);
    }
}
