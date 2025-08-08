package com.example.chatapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateUtils {

    private const val DATE_FORMAT = "dd/MM/yyyy"
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.US)


    fun formatDate(date: Date?): String {
        return date?.let { dateFormatter.format(it) } ?: ""
    }


    fun parseDate(dateString: String?): Date? {
        return try {
            dateString?.let { dateFormatter.parse(it) }
        } catch (e: Exception) {
            null
        }
    }


    fun getCurrentDateFormatted(): String {
        return formatDate(Date())
    }


    fun getCurrentDate(): Date {
        return Date()
    }


    fun isValidDate(dateString: String?): Boolean {
        return try {
            dateString?.let { dateFormatter.parse(it) } != null
        } catch (e: Exception) {
            false
        }
    }


    fun getDateFromComponents(year: Int, month: Int, day: Int): Date? {
        return try {
            val calendar = java.util.Calendar.getInstance()
            calendar.set(year, month - 1, day) // month is 0-based in Calendar
            calendar.time
        } catch (e: Exception) {
            null
        }
    }


    fun getDateComponents(date: Date?): Triple<Int, Int, Int>? {
        return date?.let {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = it
            Triple(
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1, // Convert to 1-based
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
        }
    }


    fun calculateAge(dateOfBirth: Date?): Int? {
        return dateOfBirth?.let {
            val currentDate = Date()
            val calendar = java.util.Calendar.getInstance()
            calendar.time = currentDate

            val birthCalendar = java.util.Calendar.getInstance()
            birthCalendar.time = it

            var age = calendar.get(java.util.Calendar.YEAR) - birthCalendar.get(java.util.Calendar.YEAR)

            if (calendar.get(java.util.Calendar.DAY_OF_YEAR) < birthCalendar.get(java.util.Calendar.DAY_OF_YEAR)) {
                age--
            }

            age
        }
    }


    fun calculateAgeFromString(dateOfBirthString: String?): Int? {
        val dateOfBirth = parseDate(dateOfBirthString)
        return calculateAge(dateOfBirth)
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