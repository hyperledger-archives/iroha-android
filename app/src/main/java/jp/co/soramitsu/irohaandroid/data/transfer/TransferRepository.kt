package jp.co.soramitsu.irohaandroid.data.transfer

import io.reactivex.Completable
import io.reactivex.Single
import jp.co.soramitsu.irohaandroid.presentation.model.TransactionModel
import jp.co.soramitsu.irohaandroid.presentation.model.Transfer

interface TransferRepository {

    fun getReceiveQrString(amount: String): Single<String>

    fun transfer(dstUsername: String, amount: String): Completable

    fun getTransactionHistory(): Single<List<TransactionModel>>

    fun processQr(contents: String): Single<Transfer>
}