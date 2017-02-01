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
import android.view.ViewGroup;

import click.kobaken.rxirohaandroid_sample.databinding.DialogProgressBinding;

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
