package jp.co.soramitsu.irohaandroid.presentation.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import jp.co.soramitsu.irohaandroid.IrohaSampleApp
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.presentation.base.BaseActivity
import jp.co.soramitsu.irohaandroid.presentation.base.LoadingDialog
import jp.co.soramitsu.irohaandroid.presentation.main.MainActivity
import kotlinx.android.synthetic.main.activity_signup.*
import javax.inject.Inject

class SignupActivity : BaseActivity<SignupViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SignupActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    @Inject lateinit var viewmodel: SignupViewModel
    @Inject lateinit var loader: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    override fun initViews() {
        registerButton.setOnClickListener {
            viewmodel.signupButtonPressed(usernameTextView.text.toString())
        }
    }

    override fun subscribe(viewmodel: SignupViewModel) {
        observe(viewmodel.loadingLiveData, Observer {
            if (it) loader.show() else loader.dismiss()
        })

        observe(viewmodel.registrationSuccessEventLiveData, Observer {
            MainActivity.start(this)
        })
    }

    override fun inject() {
        (application as IrohaSampleApp)
            .appComponent
            .signupComponentBuilder()
            .withActivity(this)
            .build()
            .inject(this)
    }
}
