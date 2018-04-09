package com.ecandle.raykun.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.extensions.notifyEvent
import com.ecandle.raykun.extensions.scheduleAllEvents
import com.ecandle.raykun.extensions.updateListWidget
import com.ecandle.raykun.helpers.EVENT_ID
import com.ecandle.raykun.helpers.Formatter

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Simple Calendar")
        wakelock.acquire(5000)

        context.updateListWidget()
        val id = intent.getIntExtra(EVENT_ID, -1)
        if (id == -1)
            return

        val event = context.dbHelper.getEventWithId(id)
        if (event == null || event.getReminders().isEmpty())
            return

        if (!event.ignoreEventOccurrences.contains(Formatter.getDayCodeFromTS(event.startTS).toInt())) {
            context.notifyEvent(event)
        }
        context.scheduleAllEvents()
    }
}
