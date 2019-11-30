package jp.co.soramitsu.irohaandroid.util

import android.content.Context
import android.content.SharedPreferences

class PrefsUtil(context: Context) {

    companion object {
        private const val PREFS_NAME = "iroha"
    }

    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
}