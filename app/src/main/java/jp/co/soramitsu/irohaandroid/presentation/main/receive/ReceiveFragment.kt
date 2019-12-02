package jp.co.soramitsu.irohaandroid.presentation.main.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.widget.RxTextView
import jp.co.soramitsu.irohaandroid.IrohaSampleApp

import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.presentation.base.BaseFragment
import jp.co.soramitsu.irohaandroid.presentation.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_receive.amount
import kotlinx.android.synthetic.main.fragment_receive.qr_code_image_view

class ReceiveFragment : BaseFragment<MainViewModel>() {

    companion object {
        fun newInstance(): ReceiveFragment {
            return ReceiveFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receive, container, false)
    }

    override fun initViews() {
        RxTextView.textChanges(amount)
            .subscribe { viewModel.getQrImage(it.toString()) }
    }

    override fun inject() {
        (activity!!.application as IrohaSampleApp)
            .appComponent
            .mainComponentBuilder()
            .withActivity(activity as AppCompatActivity)
            .build()
            .inject(this)
    }

    override fun subscribe(viewModel: MainViewModel) {
        observe(viewModel.qrBitmapLiveData, Observer {
            qr_code_image_view.setImageBitmap(it)
        })
    }
}
