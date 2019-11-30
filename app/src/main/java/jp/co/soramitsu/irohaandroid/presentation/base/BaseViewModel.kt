package jp.co.soramitsu.irohaandroid.presentation.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.util.IrohaException

open class BaseViewModel: ViewModel() {

    protected val disposables = CompositeDisposable()

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _errorResourceLiveData = MutableLiveData<Int>()
    val errorResourceLiveData: LiveData<Int> = _errorResourceLiveData

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    fun onError(throwable: Throwable) {
        if (throwable is IrohaException) {
            _errorLiveData.value = throwable.message
        } else {
            onError(R.string.server_is_not_reachable)
        }
    }

    fun onError(@StringRes errorMessage: Int) {
        _errorResourceLiveData.value = errorMessage
    }
}