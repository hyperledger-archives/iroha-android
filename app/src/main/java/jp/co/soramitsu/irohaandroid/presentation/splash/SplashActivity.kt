package jp.co.soramitsu.irohaandroid.presentation.splash

import androidx.lifecycle.Observer
import jp.co.soramitsu.irohaandroid.IrohaSampleApp
import jp.co.soramitsu.irohaandroid.presentation.base.BaseActivity
import jp.co.soramitsu.irohaandroid.presentation.main.MainActivity
import jp.co.soramitsu.irohaandroid.presentation.signup.SignupActivity
import jp.co.soramitsu.irohaandroid.util.RegistrationState

class SplashActivity : BaseActivity<SplashViewModel>() {

    override fun inject() {
        (application as IrohaSampleApp)
            .appComponent
            .splashComponentBuilder()
            .build()
            .inject(this)
    }

    override fun initViews() {
        viewModel.nextScreen()
    }

    override fun subscribe(viewmodel: SplashViewModel) {
        observe(viewmodel.registrationStateLiveData, Observer {
            when(it) {
                RegistrationState.INITIAL -> SignupActivity.start(this)
                RegistrationState.REGISTERED -> MainActivity.start(this)
            }
        })
    }
}
