package jp.co.soramitsu.irohaandroid.presentation.base

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import jp.co.soramitsu.irohaandroid.R
import javax.inject.Inject


class LoadingDialog @Inject constructor(context: AppCompatActivity) : ProgressDialog(context) {

    init {
        setMessage(context.getString(R.string.loading))
        setCancelable(false)
    }
}