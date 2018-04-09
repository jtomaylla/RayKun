package com.ecandle.raykun.interfaces

import java.util.*

interface DeleteTasksListener {
    fun deleteItems(ids: ArrayList<Int>)

    //fun addETaskRepeatException(parentIds: ArrayList<Int>, timestamps: ArrayList<Int>)
}
