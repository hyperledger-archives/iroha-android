package io.soramitsu.iroha.view.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.soramitsu.iroha.databinding.DialogProgressBinding;

public class ProgressDialog {
    private AlertDialog progressDialog;
    private DialogProgressBinding dialogProgressBinding;

    public ProgressDialog(LayoutInflater inflater) {
        dialogProgressBinding = DialogProgressBinding.inflate(inflater);
    }

    public void show(Activity activity, String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }

        dialogProgressBinding.message.setText(message);

        ViewGroup parent = ((ViewGroup) dialogProgressBinding.root.getParent());
        if (parent != null) {
            parent.removeView(dialogProgressBinding.root);
        }

        progressDialog = new AlertDialog.Builder(activity)
                .setView(dialogProgressBinding.root)
                .setCancelable(false)
                .create();
        progressDialog.show();
    }

    public void hide() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
