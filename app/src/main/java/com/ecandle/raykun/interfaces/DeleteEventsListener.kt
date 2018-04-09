package com.ecandle.raykun.interfaces

import java.util.*

interface DeleteEventsListener {
    fun deleteItems(ids: ArrayList<Int>)

    fun addEventRepeatException(parentIds: ArrayList<Int>, timestamps: ArrayList<Int>)
}
