package com.ecandle.raykun.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ecandle.raykun.R
import com.ecandle.raykun.adapters.ManageEventTypesAdapter
import com.ecandle.raykun.dialogs.UpdateEventTypeDialog
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.interfaces.DeleteEventTypesListener
import com.ecandle.raykun.models.EventType
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.updateTextColors
import kotlinx.android.synthetic.main.activity_manage_event_types.*
import java.util.*

class ManageEventTypesActivity : SimpleActivity(), DeleteEventTypesListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_event_types)

        getEventTypes()
        updateTextColors(manage_event_types_list)
    }

    private fun showEventTypeDialog(eventType: EventType? = null) {
        UpdateEventTypeDialog(this, eventType?.copy()) {
            getEventTypes()
        }
    }

    private fun getEventTypes() {
        dbHelper.getEventTypes {
            runOnUiThread {
                val adapter = ManageEventTypesAdapter(this, it, this, manage_event_types_list) {
                    showEventTypeDialog(it as EventType)
                }
                adapter.setupDragListener(true)
                manage_event_types_list.adapter = adapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_event_types, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_event_type -> showEventTypeDialog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun deleteEventTypes(eventTypes: ArrayList<EventType>, deleteEvents: Boolean) {
        if (eventTypes.any { it.caldavCalendarId != 0 }) {
            toast(R.string.unsync_caldav_calendar)
        }

        dbHelper.deleteEventTypes(eventTypes, deleteEvents) {
            if (it == 0) {
                toast(R.string.unknown_error_occurred)
            }
        }
    }
}
