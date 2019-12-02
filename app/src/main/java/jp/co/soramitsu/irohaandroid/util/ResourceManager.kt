package jp.co.soramitsu.irohaandroid.util

import android.content.Context

class ResourceManager(private val context: Context) {

    fun getString(resource: Int): String {
        return context.getString(resource)
    }

}