package com.ecandle.raykun.helpers

import android.content.Context
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.interfaces.WeeklyCalendar
import com.ecandle.raykun.models.Event
import java.util.*

class WeeklyCalendarImpl(val mCallback: WeeklyCalendar, val mContext: Context) {
    var mEvents = ArrayList<Event>()

    fun updateWeeklyCalendar(weekStartTS: Int) {
        val startTS = weekStartTS
        val endTS = startTS + WEEK_SECONDS
        mContext.dbHelper.getEvents(startTS, endTS) {
            mEvents = it as ArrayList<Event>
            mCallback.updateWeeklyCalendar(it)
        }
    }
}
