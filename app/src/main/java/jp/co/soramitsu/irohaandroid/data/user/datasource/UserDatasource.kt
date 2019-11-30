package jp.co.soramitsu.irohaandroid.data.user.datasource

import jp.co.soramitsu.irohaandroid.util.RegistrationState
import java.security.KeyPair

interface UserDatasource {

    fun saveRegistrationState(registrationState: RegistrationState)

    fun getRegistrationState(): RegistrationState

    fun saveUsername(username: String)

    fun getUsername(): String

    fun saveKeypair(keyPair: KeyPair)

    fun getKeypair(): KeyPair?

    fun removeUserData()

}