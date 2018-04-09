package com.ecandle.raykun.dialogs

import android.support.v7.app.AlertDialog
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.adapters.FilterEventTypeAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_filter_event_types.view.*

class FilterEventTypesDialog(val activity: SimpleActivity, val callback: () -> Unit) {
    var dialog: AlertDialog
    val view = activity.layoutInflater.inflate(R.layout.dialog_filter_event_types, null)

    init {
        val eventTypes = activity.dbHelper.fetchEventTypes()
        val displayEventTypes = activity.config.displayEventTypes
        view.filter_event_types_list.adapter = FilterEventTypeAdapter(activity, eventTypes, displayEventTypes)

        dialog = AlertDialog.Builder(activity)
                .setPositiveButton(R.string.ok, { dialogInterface, i -> confirmEventTypes() })
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
            activity.setupDialogStuff(view, this, R.string.filter_events_by_type)
        }
    }

    private fun confirmEventTypes() {
        val selectedItems = (view.filter_event_types_list.adapter as FilterEventTypeAdapter).getSelectedItemsSet()
        if (activity.config.displayEventTypes != selectedItems) {
            activity.config.displayEventTypes = selectedItems
            callback()
        }
        dialog.dismiss()
    }
}
