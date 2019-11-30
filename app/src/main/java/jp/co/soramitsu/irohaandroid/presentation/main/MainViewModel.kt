package jp.co.soramitsu.irohaandroid.presentation.main

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.domain.MainInteractor
import jp.co.soramitsu.irohaandroid.presentation.base.BaseViewModel
import jp.co.soramitsu.irohaandroid.presentation.model.TransactionModel
import jp.co.soramitsu.irohaandroid.presentation.model.Transfer
import jp.co.soramitsu.irohaandroid.presentation.model.User
import jp.co.soramitsu.irohaandroid.util.Const
import jp.co.soramitsu.irohaandroid.util.Event
import jp.co.soramitsu.irohaandroid.util.QrCodeDecoder
import jp.co.soramitsu.irohaandroid.util.QrCodeGenerator
import java.math.BigDecimal
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mainInteractor: MainInteractor,
    private val qrCodeGenerator: QrCodeGenerator,
    private val qrCodeDecoder: QrCodeDecoder
) : BaseViewModel() {

    private val _loadingEventLiveData = MutableLiveData<Boolean>()
    val loadingEventLiveData: LiveData<Boolean> = _loadingEventLiveData

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User> = _userLiveData

    private val _logoutEventLiveData = MutableLiveData<Event<Unit>>()
    val logoutEventLiveData: LiveData<Event<Unit>> = _logoutEventLiveData

    private val _qrBitmapLiveData = MutableLiveData<Bitmap>()
    val qrBitmapLiveData: LiveData<Bitmap> = _qrBitmapLiveData

    private val _transferLiveData = MutableLiveData<Transfer>()
    val transferLiveData: LiveData<Transfer> = _transferLiveData

    private val _transactionsHistoryLiveData = MutableLiveData<List<TransactionModel>>()
    val transactionsHistoryLiveData: LiveData<List<TransactionModel>> = _transactionsHistoryLiveData

    private val _bioClickedEventLiveData = MutableLiveData<Event<Unit>>()
    val bioClickedEventLiveData: LiveData<Event<Unit>> = _bioClickedEventLiveData

    private val _emptyHistoryPlacholderVisibilityLiveData = MutableLiveData<Boolean>()
    val emptyHistoryPlacholderVisibilityLiveData: LiveData<Boolean> = _emptyHistoryPlacholderVisibilityLiveData

    private val _transactionsListVisibilityLiveData = MutableLiveData<Boolean>()
    val transactionsListVisibilityLiveData: LiveData<Boolean> = _transactionsListVisibilityLiveData

    fun getUserInfo() {
        disposables.add(
            mainInteractor.getUsername()
                .flatMap { username ->
                    mainInteractor.getUserDetails()
                        .flatMap { details ->
                            mainInteractor.getBalance()
                                .map { User(username, details, it) }
                        }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _userLiveData.value = it }, { onError(it) })
        )
    }

    fun logoutClicked() {
        disposables.add(
            mainInteractor.removeUserData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _logoutEventLiveData.value = Event(Unit) }, { it.printStackTrace() })
        )
    }

    fun getQrImage(amount: String) {
        disposables.add(
            mainInteractor.generateQrJsonString(amount)
                .map { qrCodeGenerator.generateQrBitmap(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _qrBitmapLiveData.value = it }, { it.printStackTrace() })
        )
    }

    fun transfer(username: String, amount: String) {
        if (areFieldsValid(amount, _userLiveData.value?.balance, username)) {
            disposables.add(
                mainInteractor.transfer(username, amount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { _loadingEventLiveData.value = true }
                    .doFinally { _loadingEventLiveData.value = false }
                    .subscribe({ getUserInfo() }, { onError(it) })
            )
        }
    }

    fun getTransactionHistory() {
        disposables.add(
            mainInteractor.getTransactionHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _emptyHistoryPlacholderVisibilityLiveData.value = it.isEmpty()
                    _transactionsListVisibilityLiveData.value = it.isNotEmpty()

                    _transactionsHistoryLiveData.value = it
                }, { it.printStackTrace() })
        )
    }

    private fun areFieldsValid(amount: String?, balance: String?, username: String): Boolean {
        if (username.isNullOrEmpty()) {
            onError(R.string.username_empty_error)
            return false
        }

        if (amount.isNullOrEmpty()) {
            onError(R.string.amount_error)
            return false
        }

        if (amount.toDouble() <= 0) {
            onError(R.string.amount_is_zero_error)
            return false
        }

        if (BigDecimal(amount) > BigDecimal(balance ?: "0")) {
            onError(R.string.insufficient_balance)
            return false
        }

        return true
    }

    fun qrStringProcess(contents: String) {
        disposables.add(
            mainInteractor.processQr(contents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _loadingEventLiveData.value = true }
                .doFinally { _loadingEventLiveData.value = false }
                .subscribe({ _transferLiveData.value = it }, { onError(it) })
        )
    }

    fun decodeTextFromBitmapQr(uri: Uri) {
        disposables.add(
            qrCodeDecoder.decodeQrFromUri(uri)
                .flatMap { mainInteractor.processQr(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _transferLiveData.value = it }, { onError(it) })
        )
    }

    fun bioClicked() {
        _bioClickedEventLiveData.value = Event(Unit)
    }

    fun bioEntered(it: String) {
        disposables.add(
            mainInteractor.setAccountDetails(it)
                .doOnComplete { (getUserInfo()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _loadingEventLiveData.value = true }
                .doFinally { _loadingEventLiveData.value = false }
                .subscribe({}, {onError(it)})
        )
    }
}