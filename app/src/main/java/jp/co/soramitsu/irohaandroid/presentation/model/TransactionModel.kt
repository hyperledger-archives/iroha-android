package jp.co.soramitsu.irohaandroid.presentation.model

data class TransactionModel(
    val id: String,
    val prettyDate: String,
    val username: String,
    val prettyAmount: String
)