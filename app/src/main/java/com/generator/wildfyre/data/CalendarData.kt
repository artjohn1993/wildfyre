package com.generator.wildfyre.data

import java.text.SimpleDateFormat
import java.util.*

class CalendarData {

    fun getLastMonth(data : String) : String {
        var date = Calendar.getInstance()
        var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        date.add(Calendar.DAY_OF_YEAR, -data.toInt())
        var finalDate = format.format(date.time)
        return finalDate
    }

    fun processDaySheet() : String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)
        return day
    }
}