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
