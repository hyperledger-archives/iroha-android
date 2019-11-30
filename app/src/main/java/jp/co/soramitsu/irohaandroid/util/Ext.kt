package jp.co.soramitsu.irohaandroid.util

import iroha.protocol.Endpoint
import jp.co.soramitsu.iroha.java.IrohaAPI
import org.spongycastle.util.encoders.Hex

fun Endpoint.ToriiResponse.getTransactionStatus(irohaAPI: IrohaAPI, txHash: String): Endpoint.TxStatus  {
    var txStatus: Endpoint.TxStatus

    do {
        txStatus = irohaAPI.txStatusSync(Hex.decode(txHash)).txStatus
        Thread.sleep(1000)
    } while (txStatus != Endpoint.TxStatus.COMMITTED && txStatus != Endpoint.TxStatus.REJECTED)

    return  txStatus
}

fun Endpoint.ToriiResponse.getErrorString(irohaAPI: IrohaAPI, txHash: String): String {
    var response: Endpoint.ToriiResponse

    do {
        response = irohaAPI.txStatusSync(Hex.decode(txHash))
        Thread.sleep(1000)
    } while (response.txStatus != Endpoint.TxStatus.COMMITTED && response.txStatus != Endpoint.TxStatus.REJECTED)

    return if (response.txStatus == Endpoint.TxStatus.COMMITTED) "" else response.initializationErrorString
}

fun String.accountId() = "$this@${Const.DOMAIN_ID}"

fun String.username(): String {
    return if (this.isNotEmpty()) {
        this.split("@")[0]
    } else {
        this
    }
}