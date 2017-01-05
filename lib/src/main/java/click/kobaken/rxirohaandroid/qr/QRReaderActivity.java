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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import click.kobaken.rxirohaandroid.R;
import click.kobaken.rxirohaandroid.callback.Callback;

public abstract class QRReaderActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    public static final String TAG = QRReaderActivity.class.getSimpleName();

    protected static final int PERMISSION_REQUEST_CODE = 70;

    protected static Callback<String> callback;
    protected SurfaceView surfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        initialize();

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        callback = null;
        super.onDestroy();
    }

    protected abstract void initialize();
    protected abstract void onSurfaceCreated(SurfaceHolder holder);
    protected abstract void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height);
    protected abstract void onSurfaceDestroyed(SurfaceHolder holder);

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        onSurfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        onSurfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onSurfaceDestroyed(holder);
    }

    public static Callback<String> getCallback() {
        return callback;
    }

    public static void setCallback(Callback<String> callback) {
        QRReaderActivity.callback = callback;
    }
}
