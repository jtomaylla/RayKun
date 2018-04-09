package com.ecandle.raykun.interfaces

import android.util.SparseArray
import com.ecandle.raykun.models.DayYearly
import java.util.*

interface YearlyCalendar {
    fun updateYearlyCalendar(events: SparseArray<ArrayList<DayYearly>>, hashCode: Int)
}
