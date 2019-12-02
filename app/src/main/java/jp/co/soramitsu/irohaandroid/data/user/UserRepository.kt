package jp.co.soramitsu.irohaandroid.data.user

import io.reactivex.Completable
import io.reactivex.Single
import jp.co.soramitsu.irohaandroid.util.RegistrationState
import java.security.KeyPair

interface UserRepository {

    fun createAccount(username: String): Completable

    fun getRegistrationState(): Single<RegistrationState>

    fun saveUsername(username: String): Completable

    fun getUsername(): Single<String>

    fun getUserDetails(): Single<String>

    fun getUserBalance(): Single<String>

    fun getKeyPair(): Single<KeyPair>

    fun removeUserData(): Completable

    fun setAccountDetails(details: String): Completable
}