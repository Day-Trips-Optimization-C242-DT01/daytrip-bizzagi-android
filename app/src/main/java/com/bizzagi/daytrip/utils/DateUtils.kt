package com.bizzagi.daytrip.utils
import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    @SuppressLint("ConstantLocale")
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    fun formatDate(calendar: Calendar): String {
        return dateFormat.format(calendar.time)
    }

    fun parseDate(dateString: String): Calendar? {
        return try {
            val date = dateFormat.parse(dateString)
            Calendar.getInstance().apply {
                if (date != null) {
                    time = date
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
