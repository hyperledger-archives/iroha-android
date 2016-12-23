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

package io.soramitsu.iroha.view.util;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.soramitsu.iroha.R;
import io.soramitsu.iroha.util.AndroidSupportUtil;
import io.soramitsu.irohaandroid.model.Transaction;

@BindingMethods({
        @BindingMethod(type = View.class, attribute = "android:drawable", method = "setBackground"),
        @BindingMethod(type = TextView.class, attribute = "android:drawable", method = "setBackground")
})
@SuppressWarnings("unused")
public final class BindingUtils {
    @BindingAdapter({"background", "public_key", "context"})
    public static void setBackgroundDrawableByTransactionType(
            CircleImageView view, Transaction transaction, String publicKey, Context c) {

        final Drawable target;

        if (transaction.isSender(publicKey)) {
            target = AndroidSupportUtil.getDrawable(c, R.drawable.icon_send);
        } else {
            target = AndroidSupportUtil.getDrawable(c, R.drawable.icon_rec);
        }

        view.setBackground(target);
    }

    @BindingAdapter({"transaction", "public_key"})
    public static void setTransactionOpponentText(TextView textView, Transaction transaction, String publicKey) {
        String type;

        if (transaction.isSender(publicKey)) {
            type = "to ";
        } else {
            type = "from ";
        }

        String displayText = type + transaction.params.receiver;
        textView.setText(displayText);
    }
}