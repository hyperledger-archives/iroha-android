package jp.co.soramitsu.irohaandroid.presentation.main.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.soramitsu.irohaandroid.IrohaSampleApp

import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.presentation.base.BaseFragment
import jp.co.soramitsu.irohaandroid.presentation.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : BaseFragment<MainViewModel>() {

    companion object {
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun initViews() {
        viewModel.getTransactionHistory()

        refresh.setOnRefreshListener {
            viewModel.getTransactionHistory()
        }
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
        observe(viewModel.transactionsHistoryLiveData, Observer {
            if (transactions.adapter == null) {
                transactions.layoutManager = LinearLayoutManager(activity!!)
                transactions.adapter = HistoryAdapter(activity!!)
            }

            (transactions.adapter as HistoryAdapter).submitList(it)

            refresh.isRefreshing = false
        })

        observe(viewModel.emptyHistoryPlacholderVisibilityLiveData, Observer {
            transactionsPlaceholder.visibility = if (it) View.VISIBLE else View.GONE
        })

        observe(viewModel.transactionsListVisibilityLiveData, Observer {
            transactions.visibility = if (it) View.VISIBLE else View.GONE
        })
    }
}
