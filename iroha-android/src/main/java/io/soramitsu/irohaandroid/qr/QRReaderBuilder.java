package io.soramitsu.irohaandroid.qr;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import io.soramitsu.irohaandroid.callback.Callback;

public class QRReaderBuilder {
    private Context context;

    public QRReaderBuilder(Context context) {
        this.context = context;
    }

    public QRReaderBuilder setCallback(Callback<String> callback) {
        QRReaderActivity.setCallback(callback);
        return this;
    }

    public Intent build() {
        if (context == null || QRReaderActivity.getCallback() == null) {
            throw new NullPointerException();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return QRReaderHigherThanApi20Activity.getCallingIntent(context);
        } else {
            return QRReaderLowerThanApi19Activity.getCallingIntent(context);
        }
    }
}
