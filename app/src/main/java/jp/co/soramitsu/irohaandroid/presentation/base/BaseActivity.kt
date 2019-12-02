package jp.co.soramitsu.irohaandroid.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import jp.co.soramitsu.irohaandroid.R
import javax.inject.Inject

abstract class BaseActivity<T : BaseViewModel>: AppCompatActivity() {

    private val observables = mutableListOf<LiveData<*>>()
    @Inject open lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        subscribe(viewModel)

        observe(viewModel.errorLiveData, Observer {
            AlertDialog.Builder(this)
                .setTitle(R.string.general_error_title)
                .setMessage(it)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
        })

        observe(viewModel.errorResourceLiveData, Observer {
            AlertDialog.Builder(this)
                .setTitle(R.string.general_error_title)
                .setMessage(it)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
        })
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    override fun onDestroy() {
        observables.forEach { it.removeObservers(this) }
        super.onDestroy()
    }

    @Suppress("unchecked_cast")
    protected fun <V : Any?> observe(source: LiveData<V>, observer: Observer<V>) {
        source.observe(this, observer as Observer<in Any?>)
        observables.add(source)
    }

    abstract fun inject()

    abstract fun initViews()

    abstract fun subscribe(viewmodel: T)
}