package jp.co.soramitsu.irohaandroid.util

import jp.co.soramitsu.irohaandroid.R
import java.lang.RuntimeException

class IrohaException(message: String) : RuntimeException(message) {

    companion object {

        fun businessError(message: Int, resourceManager: ResourceManager): IrohaException {
            return IrohaException(resourceManager.getString(message))
        }

        fun unexpectedError(throwable: Throwable): IrohaException {
            return IrohaException(throwable.message ?: "")
        }

        fun networkException(resourceManager: ResourceManager): IrohaException {
            return IrohaException(resourceManager.getString(R.string.server_is_not_reachable))
        }

    }

}