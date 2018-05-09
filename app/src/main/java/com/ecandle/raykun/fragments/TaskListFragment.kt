package com.ecandle.raykun.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.activities.TaskActivity
import com.ecandle.raykun.adapters.TaskListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.TASK_ID
import com.ecandle.raykun.helpers.TASK_START_DATE
import com.ecandle.raykun.interfaces.DeleteTasksListener
import com.ecandle.raykun.models.ListItem
import com.ecandle.raykun.models.Task
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import kotlinx.android.synthetic.main.fragment_task_list.view.*
import java.util.*

class TaskListFragment : Fragment(), DeleteTasksListener , SearchView.OnQueryTextListener{
    private var mTasks: List<Task> = ArrayList()
    lateinit var mView: View
    private var mTaskListAdapter: TaskListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_task_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_events)}\n", getString(R.string.add_some_events))
        mView.task_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
        checkTasks()
    }

    fun checkTasks() {
        context!!.dbHelper.getTasks() {
            receivedTasks(it)
        }
    }

    private fun receivedTasks(tasks: List<Task>) {
        if (context == null || activity == null)
            return

        mTasks = tasks
        val listItems = ArrayList<ListItem>(mTasks.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mTasks.sortedWith(compareBy({ it.startdate }, { it.duedate }, { it.name }, { if (replaceDescription) it.description else it.description }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        var prevCode = ""
        sublist.forEach {
            listItems.add(Task(it.id, it.name, it.description, it.priority,it.startdate, it.duedate, it.status))
        }

        mTaskListAdapter = TaskListAdapter(activity as SimpleActivity, listItems, false, this, mView.calendar_tasks_list) {
            if (it is Task) {
                editTask(it)
            }
        }

        activity?.runOnUiThread {
            mView.calendar_tasks_list.apply {
                this@apply.adapter = mTaskListAdapter
            }
            checkPlaceholderVisibility()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_task_list_fragment, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when collapsed
                mTaskListAdapter!!.setSearchResult(mTasks)
                return true // Return true to collapse action view

            }
        })
    }
    private fun checkPlaceholderVisibility() {
        mView.task_empty_list_placeholder.beVisibleIf(mTasks.isEmpty())
        mView.calendar_tasks_list.beGoneIf(mTasks.isEmpty())
        if (activity != null)
            mView.task_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    private fun editTask(task: Task) {
        Intent(context, TaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
            putExtra(TASK_START_DATE, task.startdate)
            startActivity(this)
        }
    }
    override fun deleteItems(ids: ArrayList<Int>) {
        val eventIDs = Array(ids.size, { i -> (ids[i].toString()) })
        context!!.dbHelper.deleteTasks(eventIDs)
    }
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredTaskList = filter(mTasks, newText)
        if (newText.isEmpty()) {
            checkTasks()
        }else{
            receivedTasks(filteredTaskList)
        }
        return true
    }

    private fun filter(tasks: List<Task>, query: String): List<Task> {
        var query = query
        query = query.toLowerCase()
        val filteredTaskList = ArrayList<Task>()
        for (task in tasks) {
            val text = task.name.toLowerCase()
            if (text.contains(query)) {
                filteredTaskList.add(task)
            }
        }
        return filteredTaskList
    }


}
