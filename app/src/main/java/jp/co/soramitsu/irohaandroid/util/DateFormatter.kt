package jp.co.soramitsu.irohaandroid.util

import java.text.SimpleDateFormat
import java.util.Date

class DateFormatter {

    companion object {
        val PATTERN = "yyyy-MM-dd HH:mm"
    }

    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat(PATTERN)
        return formatter.format(date)
    }

}