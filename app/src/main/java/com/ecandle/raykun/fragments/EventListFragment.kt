package com.ecandle.raykun.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.EventActivity
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.adapters.EventListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.extensions.getFilteredEvents
import com.ecandle.raykun.extensions.seconds
import com.ecandle.raykun.helpers.EVENT_ID
import com.ecandle.raykun.helpers.EVENT_OCCURRENCE_TS
import com.ecandle.raykun.helpers.Formatter
import com.ecandle.raykun.interfaces.DeleteEventsListener
import com.ecandle.raykun.models.Event
import com.ecandle.raykun.models.ListEvent
import com.ecandle.raykun.models.ListItem
import com.ecandle.raykun.models.ListSection
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import kotlinx.android.synthetic.main.fragment_event_list.view.*
import org.joda.time.DateTime
import java.util.*

class EventListFragment : Fragment(), DeleteEventsListener {
    private var mEvents: List<Event> = ArrayList()
    private var prevEventsHash = 0
    private var lastHash = 0
    lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_event_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_events)}\n", getString(R.string.add_some_events))
        mView.calendar_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
        checkEvents()
    }

    private fun checkEvents() {
        val fromTS = DateTime().seconds() - context!!.config.displayPastEvents * 60
        val toTS = DateTime().plusYears(1).seconds()
        context!!.dbHelper.getEvents(fromTS, toTS) {
            receivedEvents(it)
        }
    }

    private fun receivedEvents(events: MutableList<Event>) {
        if (context == null || activity == null)
            return

        val newHash = events.hashCode()
        if (newHash == lastHash) {
            return
        }
        lastHash = newHash

        val filtered = context!!.getFilteredEvents(events)
        val hash = filtered.hashCode()
        if (prevEventsHash == hash)
            return

        prevEventsHash = hash
        mEvents = filtered
        val listItems = ArrayList<ListItem>(mEvents.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mEvents.sortedWith(compareBy({ it.startTS }, { it.endTS }, { it.title }, { if (replaceDescription) it.location else it.description }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        var prevCode = ""
        sublist.forEach {
            val code = Formatter.getDayCodeFromTS(it.startTS)
            if (code != prevCode) {
                val day = Formatter.getDayTitle(context!!, code)
                listItems.add(ListSection(day))
                prevCode = code
            }
            listItems.add(ListEvent(it.id, it.startTS, it.endTS, it.title, it.description, it.getIsAllDay(), it.color, it.location))
        }

        val eventsAdapter = EventListAdapter(activity as SimpleActivity, listItems, true, this, mView.calendar_events_list) {
            if (it is ListEvent) {
                editEvent(it)
            }
        }

        activity?.runOnUiThread {
            mView.calendar_events_list.apply {
                this@apply.adapter = eventsAdapter
            }
            checkPlaceholderVisibility()
        }
    }

    private fun checkPlaceholderVisibility() {
        mView.calendar_empty_list_placeholder.beVisibleIf(mEvents.isEmpty())
        mView.calendar_events_list.beGoneIf(mEvents.isEmpty())
        if (activity != null)
            mView.calendar_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    private fun editEvent(event: ListEvent) {
        Intent(context, EventActivity::class.java).apply {
            putExtra(EVENT_ID, event.id)
            putExtra(EVENT_OCCURRENCE_TS, event.startTS)
            startActivity(this)
        }
    }

    override fun deleteItems(ids: ArrayList<Int>) {
        val eventIDs = Array(ids.size, { i -> (ids[i].toString()) })
        context!!.dbHelper.deleteEvents(eventIDs, true)
        checkEvents()
    }

    override fun addEventRepeatException(parentIds: ArrayList<Int>, timestamps: ArrayList<Int>) {
        parentIds.forEachIndexed { index, value ->
            context!!.dbHelper.addEventRepeatException(value, timestamps[index])
        }
        checkEvents()
    }
}
