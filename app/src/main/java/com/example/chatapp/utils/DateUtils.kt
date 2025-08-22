package com.example.chatapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateUtils {

    private const val DATE_FORMAT = "dd/MM/yyyy"
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.US)


    private fun formatDate(date: Date?): String {
        return date?.let { dateFormatter.format(it) } ?: ""
    }


    fun getCurrentDateFormatted(): String {
        return formatDate(Date())
    }


    fun isToday(dateCalendar: Calendar): Boolean {
        val currentCalendar = Calendar.getInstance()
        return currentCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(dateCalendar: Calendar): Boolean {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.add(Calendar.DAY_OF_YEAR, -1) // Lùi lại 1 ngày
        return currentCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}