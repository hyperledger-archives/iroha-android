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

package click.kobaken.rxirohaandroid.qr;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import click.kobaken.rxirohaandroid.R;

public class QRReaderActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    public static final String TAG = QRReaderActivity.class.getSimpleName();

    protected static final int PERMISSION_REQUEST_CODE = 70;

    protected static final String ARG_LAYOUT_ID = "layout_id";

    protected static ReadQRCallback callback;

    private QRReaderHelper qrReaderHelper;

    protected SurfaceView surfaceView;

    public static Intent getCallingIntent(@NonNull Context context, @LayoutRes int layoutId) {
        Intent intent = new Intent(context, QRReaderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ARG_LAYOUT_ID, layoutId);
        return intent;
    }

    public static void setCallback(ReadQRCallback callback) {
        QRReaderActivity.callback = callback;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        qrReaderHelper.onCreate();

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }

    private void initialize() {
        int layoutId = getIntent().getExtras().getInt(ARG_LAYOUT_ID);
        setContentView(layoutId);

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);

        if (surfaceView == null) {
            throw new NullPointerException("SurfaceView with id \"surface_view\" is a required view.");
        }

        qrReaderHelper = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                ? QRReaderHelperHigherThanApi20.newInstance(this, callback, surfaceView)
                : QRReaderHelperLowerThanApi19.newInstance(this, callback, surfaceView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        qrReaderHelper.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrReaderHelper.onResume();
    }

    @Override
    protected void onPause() {
        qrReaderHelper.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        qrReaderHelper.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        qrReaderHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        qrReaderHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        qrReaderHelper.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        qrReaderHelper.surfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        qrReaderHelper.surfaceDestroyed(holder);
    }
}
