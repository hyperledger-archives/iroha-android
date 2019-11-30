package jp.co.soramitsu.irohaandroid.presentation.base

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.jakewharton.rxbinding2.widget.RxTextView
import jp.co.soramitsu.irohaandroid.R
import kotlinx.android.synthetic.main.dialog_enter_bio.view.details
import kotlinx.android.synthetic.main.dialog_enter_bio.view.symbols_left

class BioEnterDialog(
    context: Context,
    bioEnteredCallback: (String) -> Unit
) {
    companion object {
        private const val detailsLength = 32
    }

    private val dialog: AlertDialog

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_enter_bio, null)

        RxTextView.textChanges(view.details)
            .subscribe {
                view.symbols_left.text = "${detailsLength - it.length}"
            }


            dialog = AlertDialog.Builder(context).setView(view)
            .setTitle(R.string.enter_invitation_code)
            .setNegativeButton(R.string.cancel) { _, _ ->
            }
            .setPositiveButton(R.string.save) { _, _ ->
                bioEnteredCallback(view.details.text.toString())
            }
            .create()
    }

    fun show() {
        dialog.show()
    }
}