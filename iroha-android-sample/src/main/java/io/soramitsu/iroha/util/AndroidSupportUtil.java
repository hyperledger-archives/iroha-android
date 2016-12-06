package io.soramitsu.iroha.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;

import io.soramitsu.iroha.view.dialog.QRReaderApi19DialogFragment;
import io.soramitsu.iroha.view.dialog.QRReaderDialogFragment;

public class AndroidSupportUtil {
    private static final String TAG_QR_READER_DIALOG = "qr_reader_dialog";

    private AndroidSupportUtil() {
    }

    public static Drawable getDrawable(Context context, @DrawableRes int drawableId) {
        return ResourcesCompat.getDrawable(context.getResources(), drawableId, context.getTheme());
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    public static void showQRReaderDialog(FragmentManager manager) {
        DialogFragment fragment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment = QRReaderDialogFragment.newInstance();
        } else {
            fragment = QRReaderApi19DialogFragment.newInstance();
        }
        fragment.show(manager, TAG_QR_READER_DIALOG);
    }

    public static Dialog createProgressDialog(@NonNull Activity activity, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(ProgressDialog.BUTTON_NEUTRAL);
            progressDialog.setCancelable(true);
            progressDialog.create();
            return progressDialog;
        } else {
            ProgressBar progressBar = new ProgressBar(activity);
            return new AlertDialog.Builder(activity)
                    .setView(progressBar)
                    .setMessage(message)
                    .create();
        }
    }
}
