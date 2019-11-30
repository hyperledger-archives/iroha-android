package jp.co.soramitsu.irohaandroid.presentation.model.mapper

import iroha.protocol.TransactionOuterClass
import jp.co.soramitsu.irohaandroid.presentation.model.TransactionModel
import jp.co.soramitsu.irohaandroid.util.DateFormatter
import jp.co.soramitsu.irohaandroid.util.accountId
import jp.co.soramitsu.irohaandroid.util.username
import java.util.*

fun mapIrohaTransactionsToTransactions(
    transactionsDto: List<TransactionOuterClass.Transaction>,
    dateFormatter: DateFormatter,
    username: String
): List<TransactionModel> {
    val transactions = mutableListOf<TransactionModel>()

    for (transaction in transactionsDto) {
        transaction.payload.reducedPayload.getCommands(0).transferAsset.let {
            if (it.amount.isNotEmpty()) {
                transactions.add(
                    TransactionModel(
                        transaction.getSignatures(0).signature.substring(0, 8),
                        dateFormatter.formatDate(Date(transaction.payload.reducedPayload.createdTime)),
                        if (it.srcAccountId == username.accountId()) it.destAccountId.username() else it.srcAccountId.username(),
                        if (it.srcAccountId == username.accountId()) "- ${it.amount}" else it.amount
                    )
                )
            }
        }
    }

    return transactions.reversed()
}