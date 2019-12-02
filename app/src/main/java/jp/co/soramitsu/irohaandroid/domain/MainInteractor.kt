package jp.co.soramitsu.irohaandroid.domain

import io.reactivex.Completable
import io.reactivex.Single
import jp.co.soramitsu.irohaandroid.data.transfer.TransferRepository
import jp.co.soramitsu.irohaandroid.data.user.UserRepository
import jp.co.soramitsu.irohaandroid.presentation.model.TransactionModel
import jp.co.soramitsu.irohaandroid.presentation.model.Transfer
import javax.inject.Inject

class MainInteractor @Inject constructor(private val userRepository: UserRepository, private val tranferRepository: TransferRepository) {

    fun getUsername(): Single<String> {
        return userRepository.getUsername()
    }

    fun getUserDetails(): Single<String> {
        return userRepository.getUserDetails()
    }

    fun getBalance(): Single<String> {
        return userRepository.getUserBalance()
    }

    fun removeUserData(): Completable {
        return userRepository.removeUserData()
    }

    fun generateQrJsonString(amount: String): Single<String> {
        return tranferRepository.getReceiveQrString(amount)
    }

    fun transfer(username: String, amount: String): Completable {
        return tranferRepository.transfer(username, amount)
    }

    fun getTransactionHistory(): Single<List<TransactionModel>> {
        return tranferRepository.getTransactionHistory()
    }

    fun processQr(contents: String): Single<Transfer> {
        return tranferRepository.processQr(contents)
    }

    fun setAccountDetails(details: String): Completable {
        return userRepository.setAccountDetails(details)
    }

}