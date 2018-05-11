package jp.co.soramitsu.iroha.android.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.Keypair;
import jp.co.soramitsu.iroha.android.ModelCrypto;
import lombok.Getter;

public class PreferencesUtil {

    @VisibleForTesting
    public static final String SHARED_PREFERENCES_FILE = "shared_preferences_file";
    @VisibleForTesting
    public static final String SAVED_USERNAME = "saved_username";
    private static final String SAVED_PRIVATE_KEY = "saved_private_key";
    private static final String SAVED_PUBLIC_KEY = "saved_public_key";

    @Getter
    private final SharedPreferences preferences;

    private final ModelCrypto modelCrypto;

    @Inject
    public PreferencesUtil(ModelCrypto modelCrypto) {
        this.modelCrypto = modelCrypto;
        preferences = SampleApplication.instance.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public void saveUsername(String username) {
        preferences.edit().putString(SAVED_USERNAME, username).apply();
    }

    public String retrieveUsername() {
        return preferences.getString(SAVED_USERNAME, "");
    }

    public void saveKeys(Keypair keyPair) {
        preferences.edit().putString(SAVED_PUBLIC_KEY, keyPair.publicKey().hex()).apply();
        preferences.edit().putString(SAVED_PRIVATE_KEY, keyPair.privateKey().hex()).apply();
    }

    public Keypair retrieveKeys() {
        return modelCrypto.convertFromExisting(
                preferences.getString(SAVED_PUBLIC_KEY, ""),
                preferences.getString(SAVED_PRIVATE_KEY, "")
        );
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}