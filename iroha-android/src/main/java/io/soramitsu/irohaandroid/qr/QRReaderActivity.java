package io.soramitsu.irohaandroid.qr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import io.soramitsu.irohaandroid.R;
import io.soramitsu.irohaandroid.callback.Callback;

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
