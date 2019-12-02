package jp.co.soramitsu.irohaandroid.presentation.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.co.soramitsu.irohaandroid.domain.SignupInteractor
import jp.co.soramitsu.irohaandroid.presentation.base.BaseViewModel
import jp.co.soramitsu.irohaandroid.util.Event
import javax.inject.Inject

class SignupViewModel @Inject constructor(var signupInteractor: SignupInteractor) :
    BaseViewModel() {

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    private val _registrationSuccessEventLiveData = MutableLiveData<Event<Unit>>()
    val registrationSuccessEventLiveData: LiveData<Event<Unit>> = _registrationSuccessEventLiveData

    fun signupButtonPressed(username: String) {
        if (username.isEmpty()) {
            return
        }

        disposables.add(
            signupInteractor.createAccount(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _loadingLiveData.value = true }
                .doFinally { _loadingLiveData.value = false }
                .subscribe(
                    { _registrationSuccessEventLiveData.value = Event(Unit) },
                    { onError(it) }
                )
        )
    }
}