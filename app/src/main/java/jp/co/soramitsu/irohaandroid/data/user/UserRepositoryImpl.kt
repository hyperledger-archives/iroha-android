package jp.co.soramitsu.irohaandroid.data.user

import io.reactivex.Completable
import io.reactivex.Single
import iroha.protocol.Endpoint
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import jp.co.soramitsu.iroha.java.IrohaAPI
import jp.co.soramitsu.iroha.java.Query
import jp.co.soramitsu.iroha.java.Transaction
import jp.co.soramitsu.iroha.java.TransactionStatusObserver
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.data.user.datasource.UserDatasource
import jp.co.soramitsu.irohaandroid.util.*
import org.json.JSONObject
import org.spongycastle.util.encoders.Hex
import java.security.KeyPair
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val irohaAPI: IrohaAPI,
    private val datasource: UserDatasource,
    private val ed25519Sha3: Ed25519Sha3,
    private val resourceManager: ResourceManager
): UserRepository {

    companion object {
        private const val AMOUNT = "1337"
        private const val INITIAL = "initial"
        private const val DETAILS_KEY = "details"
    }

    override fun createAccount(username: String): Completable {
        return Completable.create { emitter ->
            val keyPair = ed25519Sha3.generateKeypair()

            val tx1 = Transaction.builder(Const.CREATOR)
                .createAccount(username, Const.DOMAIN_ID, keyPair.public)
                .addAssetQuantity(Const.ASSET_ID,
                    AMOUNT
                )
                .transferAsset(Const.CREATOR, username.accountId(), Const.ASSET_ID,
                    INITIAL,
                    AMOUNT
                )
                .sign(Ed25519Sha3.keyPairFromBytes(Hex.decode(Const.PRIV_KEY), Hex.decode(Const.PUB_KEY)))
                .build()

            val observer = TransactionStatusObserver.builder()
                .onTransactionFailed {
                    val exception = when (it.txStatus) {

                        Endpoint.TxStatus.STATELESS_VALIDATION_FAILED -> IrohaException.businessError(
                            R.string.username_not_valid, resourceManager)

                        Endpoint.TxStatus.STATEFUL_VALIDATION_FAILED -> IrohaException.businessError(
                            R.string.username_already_exists_error, resourceManager
                        )

                        else -> IrohaException("")
                    }

                    emitter.onError(exception)
                }
                .onTransactionCommitted {
                    datasource.saveUsername(username)
                    datasource.saveKeypair(keyPair)
                    datasource.saveRegistrationState(RegistrationState.REGISTERED)
                    emitter.onComplete()
                }
                .build()

            irohaAPI.transaction(tx1).blockingSubscribe(observer)
        }
    }

    override fun getUserDetails(): Single<String> {
        return Single.create {
            val keyPair = datasource.getKeypair()!!
            val username = datasource.getUsername()

            val query = Query.builder(username.accountId(), Const.QUERY_COUNTER)
                .getAccountDetail(username.accountId(), null, null)
                .buildSigned(keyPair)

            val response = irohaAPI.query(query)

            val detailsJson = JSONObject(response.accountDetailResponse.detail)

            if (detailsJson.has(username.accountId()) && detailsJson.getJSONObject(username.accountId()).has(DETAILS_KEY)) {
                it.onSuccess(detailsJson.getJSONObject(username.accountId()).getString(DETAILS_KEY))
            } else {
                it.onSuccess("")
            }
        }
    }

    override fun getUserBalance(): Single<String> {
        return Single.create {
            val keyPair = datasource.getKeypair()
            val username = datasource.getUsername()

            val query = Query.builder(username.accountId(), Const.QUERY_COUNTER)
                .getAccountAssets(username.accountId())
                .buildSigned(keyPair)

            val response = irohaAPI.query(query)

            val balance = response.accountAssetsResponse.getAccountAssets(0).balance
            it.onSuccess(balance)
        }
    }

    override fun getRegistrationState(): Single<RegistrationState> {
        return Single.just(datasource.getRegistrationState())
    }

    override fun saveUsername(username: String): Completable {
        return Completable.fromAction { datasource.saveUsername(username) }
    }

    override fun getUsername(): Single<String> {
        return Single.just(datasource.getUsername())
    }

    override fun removeUserData(): Completable {
        return Completable.fromAction { datasource.removeUserData() }
    }

    override fun getKeyPair(): Single<KeyPair> {
        return Single.just(datasource.getKeypair())
    }

    override fun setAccountDetails(details: String): Completable {
        return Completable.create { emitter ->
            val username = datasource.getUsername()
            val keyPair = datasource.getKeypair()

            val tx1 = Transaction.builder(datasource.getUsername().accountId())
                .setAccountDetail(username.accountId(), DETAILS_KEY, details)
                .sign(keyPair)
                .build()

            val observer = TransactionStatusObserver.builder()
                .onTransactionFailed {
                    emitter.onError(IrohaException.businessError(R.string.general_error, resourceManager))
                }
                .onTransactionCommitted { emitter.onComplete() }
                .build()

            irohaAPI.transaction(tx1).blockingSubscribe(observer)
        }
    }
}