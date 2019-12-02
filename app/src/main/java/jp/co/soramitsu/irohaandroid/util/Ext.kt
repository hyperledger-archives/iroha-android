package jp.co.soramitsu.irohaandroid.util

fun String.accountId() = "$this@${Const.DOMAIN_ID}"

fun String.username(): String {
    return if (this.isNotEmpty()) {
        this.split("@")[0]
    } else {
        this
    }
}