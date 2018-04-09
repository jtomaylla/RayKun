package com.ecandle.raykun.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.ecandle.raykun.adapters.EventListWidgetAdapter

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = EventListWidgetAdapter(applicationContext)
}
