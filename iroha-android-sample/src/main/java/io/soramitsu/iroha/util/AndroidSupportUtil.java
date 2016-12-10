package io.soramitsu.iroha.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

public class AndroidSupportUtil {
    private AndroidSupportUtil() {
    }

    public static Drawable getDrawable(Context context, @DrawableRes int drawableId) {
        return ResourcesCompat.getDrawable(context.getResources(), drawableId, context.getTheme());
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }
}
