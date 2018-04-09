package com.ecandle.raykun.dialogs

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.dbHelper
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_delete_task.view.*

class DeleteTaskDialog(val activity: Activity, taskIds: List<Int>, val callback: (allOccurrences: Boolean) -> Unit) {
    val dialog: AlertDialog?

    init {
        val tasks = activity.dbHelper.getTasksWithIds(taskIds)
        val hasRepeatableTask = false

        val view = activity.layoutInflater.inflate(R.layout.dialog_delete_task, null).apply {
            //delete_task_repeat_description.beVisibleIf(hasRepeatableEvent)
            delete_task_radio_view.beVisibleIf(hasRepeatableTask)

//            if (taskIds.size > 1) {
//                delete_task_repeat_description.text = resources.getString(R.string.selection_contains_repetition)
//            }
        }

        dialog = AlertDialog.Builder(activity)
                .setPositiveButton(R.string.yes, { dialog, which -> dialogConfirmed(view as ViewGroup, hasRepeatableTask) })
                .setNegativeButton(R.string.no, null)
                .create().apply {
            activity.setupDialogStuff(view, this)
        }
    }

    private fun dialogConfirmed(view: ViewGroup, hasRepeatableEvent: Boolean) {
        val deleteAllOccurrences = !hasRepeatableEvent || view.delete_task_radio_view.checkedRadioButtonId == R.id.delete_task_all
        dialog?.dismiss()
        callback(deleteAllOccurrences)
    }
}
