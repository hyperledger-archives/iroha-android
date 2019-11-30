package jp.co.soramitsu.irohaandroid.domain

import io.reactivex.Completable
import io.reactivex.Single
import jp.co.soramitsu.irohaandroid.data.user.UserRepository
import jp.co.soramitsu.irohaandroid.util.RegistrationState
import javax.inject.Inject

class SignupInteractor @Inject constructor(private val userRepository: UserRepository) {

    fun createAccount(username: String): Completable {
        return userRepository.createAccount(username)
    }

    fun getRegistrationState(): Single<RegistrationState> {
        return userRepository.getRegistrationState()
    }
}