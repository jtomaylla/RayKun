package com.ecandle.raykun.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ecandle.raykun.extensions.notifyRunningEvents
import com.ecandle.raykun.extensions.recheckCalDAVCalendars
import com.ecandle.raykun.extensions.scheduleAllEvents

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {
        context.apply {
            scheduleAllEvents()
            notifyRunningEvents()
            recheckCalDAVCalendars {}
        }
    }
}
