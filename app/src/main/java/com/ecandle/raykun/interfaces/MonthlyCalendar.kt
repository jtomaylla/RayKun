package com.ecandle.raykun.interfaces

import android.content.Context
import com.ecandle.raykun.models.DayMonthly

interface MonthlyCalendar {
    fun updateMonthlyCalendar(context: Context, month: String, days: List<DayMonthly>, checkedEvents: Boolean)
}
