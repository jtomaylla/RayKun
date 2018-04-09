package com.ecandle.raykun.extensions

import com.ecandle.raykun.helpers.Formatter
import com.ecandle.raykun.helpers.MONTH
import com.ecandle.raykun.helpers.WEEK
import com.ecandle.raykun.models.Event

fun Int.isTsOnProperDay(event: Event): Boolean {
    val dateTime = Formatter.getDateTimeFromTS(this)
    val power = Math.pow(2.0, (dateTime.dayOfWeek - 1).toDouble()).toInt()
    return event.repeatRule and power != 0
}

fun Int.isXWeeklyRepetition() = this != 0 && this % WEEK == 0

fun Int.isXMonthlyRepetition() = this != 0 && this % MONTH == 0
