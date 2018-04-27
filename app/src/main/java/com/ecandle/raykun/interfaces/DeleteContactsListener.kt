package com.ecandle.raykun.interfaces

import java.util.*

interface DeleteContactsListener {
    fun deleteContacts(ids: ArrayList<Int>)

    //fun addETaskRepeatException(parentIds: ArrayList<Int>, timestamps: ArrayList<Int>)
}
