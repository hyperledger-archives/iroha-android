/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

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

package click.kobaken.rxirohaandroid_sample.view.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import click.kobaken.rxirohaandroid_sample.R;
import click.kobaken.rxirohaandroid_sample.databinding.DialogBinding;
import click.kobaken.rxirohaandroid_sample.util.AndroidSupportUtil;

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
