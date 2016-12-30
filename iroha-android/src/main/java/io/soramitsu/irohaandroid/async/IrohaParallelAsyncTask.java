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

import java.util.concurrent.CountDownLatch;

public abstract class IrohaParallelAsyncTask<T> extends BaseIrohaAsyncTask<T> {
    public static final String TAG = IrohaParallelAsyncTask.class.getSimpleName();

    private final CountDownLatch countDownLatch;

    private DataSet target;

    protected IrohaParallelAsyncTask(
            DataSet target,
            CountDownLatch countDownLatch) {

        this.target = target;
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void onMainThread() {
        if (countDownLatch.getCount() == 3) {
            target.setT1(result);
        } else if (countDownLatch.getCount() == 2) {
            if (target.getT1() == null) {
                target.setT1(result);
            } else {
                target.setT2(result);
            }
        } else if (countDownLatch.getCount() == 1) {
            if (target.getT2() == null) {
                target.setT2(result);
            } else {
                target.setT3(result);
            }
        }

        countDownLatch.countDown();
    }
}
