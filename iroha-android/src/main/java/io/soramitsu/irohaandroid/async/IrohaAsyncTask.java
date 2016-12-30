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

package io.soramitsu.irohaandroid.async;

import android.util.Log;

import io.soramitsu.irohaandroid.callback.Callback;

public abstract class IrohaAsyncTask<T> extends BaseIrohaAsyncTask<T> {
    public static final String TAG = IrohaAsyncTask.class.getSimpleName();

    private final Callback<T> callback;

    protected IrohaAsyncTask(Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    protected void onMainThread() {
        if (exception != null) {
            Log.e(TAG, "Iroha:  throw exception!", exception);
            callback.onFailure(exception);
            return;
        }

        callback.onSuccessful(result);
    }
}
