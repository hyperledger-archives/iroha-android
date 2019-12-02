package jp.co.soramitsu.irohaandroid.presentation.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import jp.co.soramitsu.irohaandroid.R

class ChooserDialog(val context: Context, cameraClick: () -> Unit, galleryClick: () -> Unit) {

    private val instance: Dialog

    init {
        instance = AlertDialog.Builder(context)
            .setTitle(R.string.qr_chooser_title)
            .setItems(R.array.qr_dialog_array) { _, item ->
                when (item) {
                    0 -> cameraClick()
                    1 -> galleryClick()
                }
            }
            .setCancelable(true)
            .create()
    }

    fun show() {
        instance.show()
    }

    fun dismiss() {
        instance.dismiss()
    }

}