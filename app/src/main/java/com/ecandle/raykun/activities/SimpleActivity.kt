package com.ecandle.raykun.activities

import com.ecandle.raykun.R
import com.ecandle.raykun.dialogs.CustomEventReminderDialog
import com.ecandle.raykun.dialogs.CustomEventRepeatIntervalDialog
import com.ecandle.raykun.extensions.getFormattedMinutes
import com.ecandle.raykun.extensions.getRepetitionText
import com.ecandle.raykun.helpers.DAY
import com.ecandle.raykun.helpers.MONTH
import com.ecandle.raykun.helpers.WEEK
import com.ecandle.raykun.helpers.YEAR
import com.simplemobiletools.commons.dialogs.RadioGroupDialog
import com.simplemobiletools.commons.extensions.hideKeyboard
import com.simplemobiletools.commons.models.RadioItem
import java.util.TreeSet
import kotlin.collections.ArrayList

open class SimpleActivity : MyBaseSimpleActivity() {
    protected fun showEventReminderDialog(curMinutes: Int, callback: (minutes: Int) -> Unit) {
        hideKeyboard()
        val minutes = TreeSet<Int>()
        minutes.apply {
            add(-1)
            add(0)
            add(10)
            add(30)
            add(curMinutes)
        }

        val items = ArrayList<RadioItem>(minutes.size + 1)
        minutes.mapIndexedTo(items, { index, value ->
            RadioItem(index, getFormattedMinutes(value), value)
        })

        var selectedIndex = 0
        minutes.forEachIndexed { index, value ->
            if (value == curMinutes)
                selectedIndex = index
        }

        items.add(RadioItem(-2, getString(R.string.custom)))

        RadioGroupDialog(this, items, selectedIndex) {
            if (it == -2) {
                CustomEventReminderDialog(this) {
                    callback(it)
                }
            } else {
                callback(it as Int)
            }
        }
    }

    protected fun showEventRepeatIntervalDialog(curSeconds: Int, callback: (minutes: Int) -> Unit) {
        hideKeyboard()
        val seconds = TreeSet<Int>()
        seconds.apply {
            add(0)
            add(DAY)
            add(WEEK)
            add(MONTH)
            add(YEAR)
            add(curSeconds)
        }

        val items = ArrayList<RadioItem>(seconds.size + 1)
        seconds.mapIndexedTo(items, { index, value ->
            RadioItem(index, getRepetitionText(value), value)
        })

        var selectedIndex = 0
        seconds.forEachIndexed { index, value ->
            if (value == curSeconds)
                selectedIndex = index
        }

        items.add(RadioItem(-1, getString(R.string.custom)))

        RadioGroupDialog(this, items, selectedIndex) {
            if (it == -1) {
                CustomEventRepeatIntervalDialog(this) {
                    callback(it)
                }
            } else {
                callback(it as Int)
            }
        }
    }
}
