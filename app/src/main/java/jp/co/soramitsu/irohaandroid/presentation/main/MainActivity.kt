package jp.co.soramitsu.irohaandroid.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import jp.co.soramitsu.irohaandroid.IrohaSampleApp
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.presentation.base.BaseActivity
import jp.co.soramitsu.irohaandroid.presentation.base.BioEnterDialog
import jp.co.soramitsu.irohaandroid.presentation.base.LoadingDialog
import jp.co.soramitsu.irohaandroid.presentation.main.history.HistoryFragment
import jp.co.soramitsu.irohaandroid.presentation.main.receive.ReceiveFragment
import jp.co.soramitsu.irohaandroid.presentation.main.send.SendFragment
import jp.co.soramitsu.irohaandroid.presentation.signup.SignupActivity
import jp.co.soramitsu.irohaandroid.util.Const
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel>() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    @Inject lateinit var loader: LoadingDialog

    private lateinit var dialog: BioEnterDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun inject() {
        (application as IrohaSampleApp)
            .appComponent
            .mainComponentBuilder()
            .withActivity(this)
            .build()
            .inject(this)
    }

    override fun initViews() {
        dialog = BioEnterDialog(this) { viewModel.bioEntered(it) }

        viewModel.getUserInfo()

        logout.setOnClickListener { viewModel.logoutClicked() }

        bio.setOnClickListener { viewModel.bioClicked() }

        val adapter = TabsViewpagerAdapter(supportFragmentManager).apply {
            addPage(getString(R.string.send), SendFragment.newInstance())
            addPage(getString(R.string.receive), ReceiveFragment.newInstance())
            addPage(getString(R.string.history), HistoryFragment.newInstance())
        }
        contentPager.adapter = adapter
        tabs.setupWithViewPager(contentPager)
    }

    override fun subscribe(viewmodel: MainViewModel) {
        observe(viewmodel.userLiveData, Observer {
            username.text = it.username

            if (it.userDetails.isNotEmpty()) {
                bio.text = it.userDetails
            }
            balance.text = "${it.balance} ${Const.ASSET_NAME}"
        })

        observe(viewmodel.loadingEventLiveData, Observer {
            if (it) loader.show() else loader.dismiss()
        })

        observe(viewmodel.logoutEventLiveData, Observer {
            SignupActivity.start(this)
        })

        observe(viewmodel.bioClickedEventLiveData, Observer {
            dialog.show()
        })
    }
}
