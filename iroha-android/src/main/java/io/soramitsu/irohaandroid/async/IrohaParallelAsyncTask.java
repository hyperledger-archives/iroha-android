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

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import io.soramitsu.irohaandroid.callback.Function;

public class IrohaParallelAsyncTask<T> extends AsyncTask<Void, Void, T> {
    public static final String TAG = IrohaParallelAsyncTask.class.getSimpleName();

    private final Function<? extends T> function;
    private final CountDownLatch countDownLatch;

    private DataSet target;
    private Exception exception;


    public IrohaParallelAsyncTask(
            DataSet target,
            Function<? extends T> function,
            CountDownLatch countDownLatch) {

        this.target = target;
        this.function = function;
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected T doInBackground(Void... ts) {
        try {
            return function.call();
        } catch (Exception e) {
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(T res) {
        super.onPostExecute(res);

        if (countDownLatch.getCount() == 3) {
            target.setT1(res);
        } else if (countDownLatch.getCount() == 2) {
            if (target.getT1() == null) {
                target.setT1(res);
            } else {
                target.setT2(res);
            }
        } else if (countDownLatch.getCount() == 1) {
            if (target.getT2() == null) {
                target.setT2(res);
            } else {
                target.setT3(res);
            }
        }

        countDownLatch.countDown();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, "onCancelled: ");
    }
}
