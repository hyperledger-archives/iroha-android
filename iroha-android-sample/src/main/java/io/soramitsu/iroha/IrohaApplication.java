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

package io.soramitsu.iroha;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class IrohaApplication extends Application {
    public static final String SHARED_PREF_FILE = "preferences";
    public static final String SHARED_PREF_REGISTERED_KEY = "registered";

    public static SharedPreferences getSharedPreferences(@NonNull final Context context) {
        return context.getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
    }

    public static boolean isRegistered(@NonNull final Context context) {
        return getSharedPreferences(context).getBoolean(SHARED_PREF_REGISTERED_KEY, false);
    }

    public static void applyRegistered(@NonNull final Context context, final boolean isRegistered) {
        getSharedPreferences(context).edit().putBoolean(SHARED_PREF_REGISTERED_KEY, isRegistered).apply();
    }
}
