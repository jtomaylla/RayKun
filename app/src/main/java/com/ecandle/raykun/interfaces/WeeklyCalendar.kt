package com.ecandle.raykun.interfaces

import com.ecandle.raykun.models.Event

interface WeeklyCalendar {
    fun updateWeeklyCalendar(events: ArrayList<Event>)
}
