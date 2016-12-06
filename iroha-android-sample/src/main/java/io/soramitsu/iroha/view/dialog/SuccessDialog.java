package io.soramitsu.iroha.view.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.DialogBinding;
import io.soramitsu.iroha.util.AndroidSupportUtil;

public class SuccessDialog {
    private AlertDialog dialog;
    private DialogBinding dialogSuccessfulBinding;

    public SuccessDialog(LayoutInflater inflater) {
        dialogSuccessfulBinding = DialogBinding.inflate(inflater);
    }

    public void show(Activity activity, String title, String message, View.OnClickListener clickListener) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

//        dialogSuccessfulBinding.image.setImageDrawable(
//                AndroidSupportUtil.getDrawable(activity, R.drawable.tibi2)
//        );
        dialogSuccessfulBinding.title.setText(title);
        dialogSuccessfulBinding.title.setTextColor(
                AndroidSupportUtil.getColor(activity.getApplicationContext(), R.color.colorAccent)
        );
        dialogSuccessfulBinding.message.setText(message);
        dialogSuccessfulBinding.ok.setTextColor(
                AndroidSupportUtil.getColor(activity.getApplicationContext(), R.color.red600)
        );
        dialogSuccessfulBinding.ok.setOnClickListener(clickListener);

        ViewGroup parent = ((ViewGroup) dialogSuccessfulBinding.root.getParent());
        if (parent != null) {
            parent.removeView(dialogSuccessfulBinding.root);
        }

        dialog = new AlertDialog.Builder(activity)
                .setView(dialogSuccessfulBinding.root)
                .create();
        dialog.show();
    }

    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
