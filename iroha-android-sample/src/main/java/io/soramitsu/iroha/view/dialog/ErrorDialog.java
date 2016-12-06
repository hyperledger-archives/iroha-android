package io.soramitsu.iroha.view.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.DialogBinding;
import io.soramitsu.iroha.util.AndroidSupportUtil;

public class ErrorDialog {
    private AlertDialog dialog;
    private DialogBinding dialogErrorBinding;

    public ErrorDialog(LayoutInflater inflater) {
        dialogErrorBinding = DialogBinding.inflate(inflater);
    }

    public void show(Activity activity, String message) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

//        dialogSuccessfulBinding.image.setImageDrawable(
//                AndroidSupportUtil.getDrawable(activity, R.drawable.tibi2)
//        );
        dialogErrorBinding.title.setText(activity.getApplicationContext().getString(R.string.error));
        dialogErrorBinding.title.setTextColor(
                AndroidSupportUtil.getColor(activity.getApplicationContext(), R.color.colorAccent)
        );
        dialogErrorBinding.message.setText(message);
        dialogErrorBinding.ok.setTextColor(
                AndroidSupportUtil.getColor(activity.getApplicationContext(), R.color.red600)
        );
        dialogErrorBinding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ViewGroup parent = ((ViewGroup) dialogErrorBinding.root.getParent());
        if (parent != null) {
            parent.removeView(dialogErrorBinding.root);
        }

        dialog = new AlertDialog.Builder(activity)
                .setView(dialogErrorBinding.root)
                .create();
        dialog.show();
    }
}
