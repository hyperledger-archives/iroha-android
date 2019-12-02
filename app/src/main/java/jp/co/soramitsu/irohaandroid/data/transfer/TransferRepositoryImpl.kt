package jp.co.soramitsu.irohaandroid.data.transfer

import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import iroha.protocol.Endpoint
import jp.co.soramitsu.iroha.java.IrohaAPI
import jp.co.soramitsu.iroha.java.Query
import jp.co.soramitsu.iroha.java.Transaction
import jp.co.soramitsu.iroha.java.TransactionStatusObserver
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.data.user.datasource.UserDatasource
import jp.co.soramitsu.irohaandroid.presentation.model.TransactionModel
import jp.co.soramitsu.irohaandroid.presentation.model.Transfer
import jp.co.soramitsu.irohaandroid.presentation.model.mapper.mapIrohaTransactionsToTransactions
import jp.co.soramitsu.irohaandroid.util.*

class TransferRepositoryImpl(
    private val irohaAPI: IrohaAPI,
    private val gson: Gson,
    private val userDatasource: UserDatasource,
    private val dateFormatter: DateFormatter,
    private val resourceManager: ResourceManager
) : TransferRepository {

    companion object {
        private const val PAGE_SIZE = 100
    }

    override fun transfer(dstUsername: String, amount: String): Completable {
        return Completable.create { emitter ->
            val srcUsername = userDatasource.getUsername()
            val keyPair = userDatasource.getKeypair()

            val tx1 = Transaction.builder(srcUsername.accountId())
                .transferAsset(
                    srcUsername.accountId(),
                    dstUsername.accountId(),
                    Const.ASSET_ID,
                    "",
                    amount
                )
                .sign(keyPair)
                .build()

            val observer = TransactionStatusObserver.builder()
                .onTransactionFailed {
                    val exception = when (it.txStatus) {

                        Endpoint.TxStatus.STATELESS_VALIDATION_FAILED -> IrohaException.businessError(
                            R.string.username_not_valid, resourceManager
                        )

                        Endpoint.TxStatus.STATEFUL_VALIDATION_FAILED -> IrohaException.businessError(
                            R.string.username_doesnt_exists, resourceManager
                        )

                        else -> IrohaException("")
                    }

                    emitter.onError(exception)
                }
                .onError { emitter.onError(IrohaException.unexpectedError(it)) }
                .onTransactionCommitted { emitter.onComplete() }
                .build()

            irohaAPI.transaction(tx1).blockingSubscribe(observer)
        }
    }

    override fun getReceiveQrString(amount: String): Single<String> {
        val username = userDatasource.getUsername()
        return Single.just(gson.toJson(Transfer(amount, username.accountId())))
    }

    override fun getTransactionHistory(): Single<List<TransactionModel>> {
        return Single.create { emitter ->
            val username = userDatasource.getUsername()
            val keyPair = userDatasource.getKeypair()

            val query = Query.builder(username.accountId(), Const.QUERY_COUNTER)
                .getAccountAssetTransactions(username.accountId(), Const.ASSET_ID, PAGE_SIZE, null)
                .buildSigned(keyPair)

            val result = irohaAPI.query(query)

            val transactionsDto = result.transactionsPageResponse.transactionsList

            emitter.onSuccess(
                mapIrohaTransactionsToTransactions(
                    transactionsDto,
                    dateFormatter,
                    username
                )
            )
        }
    }

    override fun processQr(contents: String): Single<Transfer> {
        return Single.just(gson.fromJson(contents, Transfer::class.java))
    }
}