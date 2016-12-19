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
            target = AndroidSupportUtil.getDrawable(c, R.drawable.icon_rec);
        } else {
            target = AndroidSupportUtil.getDrawable(c, R.drawable.icon_send);
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