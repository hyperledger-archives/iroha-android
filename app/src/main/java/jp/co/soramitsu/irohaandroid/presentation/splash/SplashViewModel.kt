package jp.co.soramitsu.irohaandroid.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.co.soramitsu.irohaandroid.domain.SignupInteractor
import jp.co.soramitsu.irohaandroid.presentation.base.BaseViewModel
import jp.co.soramitsu.irohaandroid.util.RegistrationState
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val signupInteractor: SignupInteractor): BaseViewModel() {

    private val _registrationStateLiveData = MutableLiveData<RegistrationState>()
    val registrationStateLiveData: LiveData<RegistrationState> = _registrationStateLiveData

    fun nextScreen() {
        signupInteractor.getRegistrationState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({_registrationStateLiveData.value = it}, { it.printStackTrace() })
    }

}