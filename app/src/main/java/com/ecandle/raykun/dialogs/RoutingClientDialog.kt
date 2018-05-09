package com.ecandle.raykun.dialogs

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.dbHelper
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_routing_client.view.*

class RoutingClientDialog(val activity: Activity, clientIds: List<Int>, val callback: (allOccurrences: Boolean) -> Unit) {
    val dialog: AlertDialog?

    init {
        val clients = activity.dbHelper.getClientsWithIds(clientIds)
        val hasRepeatableClient = false

        val view = activity.layoutInflater.inflate(R.layout.dialog_routing_client, null).apply {
            //delete_event_repeat_description.beVisibleIf(hasRepeatableEvent)
            routing_client_radio_view.beVisibleIf(hasRepeatableClient)

//            if (clientIds.size > 1) {
//                delete_event_repeat_description.text = resources.getString(R.string.selection_contains_repetition)
//            }
        }

        dialog = AlertDialog.Builder(activity)
                .setPositiveButton(R.string.yes, { dialog, which -> dialogConfirmed(view as ViewGroup, hasRepeatableClient) })
                .setNegativeButton(R.string.no, null)
                .create().apply {
            activity.setupDialogStuff(view, this)
        }
    }

    private fun dialogConfirmed(view: ViewGroup, hasRepeatableEvent: Boolean) {
        val routingAllOccurrences = !hasRepeatableEvent || view.routing_client_radio_view.checkedRadioButtonId == R.id.routing_client_all
        dialog?.dismiss()
        callback(routingAllOccurrences)
    }
}
