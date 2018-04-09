package com.ecandle.raykun.interfaces

import com.ecandle.raykun.models.EventType
import java.util.*

interface DeleteEventTypesListener {
    fun deleteEventTypes(eventTypes: ArrayList<EventType>, deleteEvents: Boolean)
}
