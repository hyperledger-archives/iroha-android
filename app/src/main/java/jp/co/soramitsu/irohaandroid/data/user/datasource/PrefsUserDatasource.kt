package jp.co.soramitsu.irohaandroid.data.user.datasource

import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import jp.co.soramitsu.irohaandroid.util.PrefsUtil
import jp.co.soramitsu.irohaandroid.util.RegistrationState
import org.spongycastle.util.encoders.Hex
import java.security.KeyPair
import javax.inject.Inject

class PrefsUserDatasource @Inject constructor(private val prefsUtil: PrefsUtil):
    UserDatasource {

    companion object {
        private const val KEY_REGISTRATION_STATE = "key_registration_state"
        private const val KEY_USERNAME = "key_username"
        private const val KEY_PRIVATE = "key_private"
        private const val KEY_PUBLIC = "key_public"
    }

    override fun saveRegistrationState(registrationState: RegistrationState) {
        prefsUtil.saveString(KEY_REGISTRATION_STATE, registrationState.toString())
    }

    override fun getRegistrationState(): RegistrationState {
        val registrationStateString = prefsUtil.getString(KEY_REGISTRATION_STATE, "")

        return if (registrationStateString.isEmpty()) {
            RegistrationState.INITIAL
        } else {
            RegistrationState.valueOf(registrationStateString)
        }
    }

    override fun saveUsername(username: String) {
        prefsUtil.saveString(KEY_USERNAME, username)
    }

    override fun getUsername(): String {
        return prefsUtil.getString(KEY_USERNAME, "")
    }

    override fun saveKeypair(keyPair: KeyPair) {
        prefsUtil.saveString(KEY_PUBLIC, Hex.toHexString(keyPair.public.encoded))
        prefsUtil.saveString(KEY_PRIVATE, Hex.toHexString(keyPair.private.encoded))
    }

    override fun getKeypair(): KeyPair? {
        val publicString = prefsUtil.getString(KEY_PUBLIC, "")
        val privateString = prefsUtil.getString(KEY_PRIVATE, "")

        return if (publicString.isEmpty() || privateString.isEmpty()) {
            null
        } else {
            Ed25519Sha3.keyPairFromBytes(Hex.decode(privateString), Hex.decode(publicString))
        }
    }

    override fun removeUserData() {
        prefsUtil.clearData()
    }
}