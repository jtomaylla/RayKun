package com.ecandle.raykun.adapters

//import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.dialogs.DeleteEventDialog
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.shareEvents
import com.ecandle.raykun.helpers.Formatter
import com.ecandle.raykun.interfaces.DeleteTasksListener
import com.ecandle.raykun.models.ListEvent
import com.ecandle.raykun.models.ListItem
import com.ecandle.raykun.models.ListSection
import com.ecandle.raykun.models.Task
import com.simplemobiletools.commons.extensions.beInvisibleIf
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.task_list_item.view.*
import java.util.*

class TaskListAdapter(activity: SimpleActivity, val listItems: ArrayList<ListItem>, val allowLongClick: Boolean, val listener: DeleteTasksListener?,
                      recyclerView: MyRecyclerView, itemClick: (Any) -> Unit) : MyRecyclerViewAdapter(activity, recyclerView, itemClick) {

    private val ITEM_EVENT = 0
    private val ITEM_HEADER = 1

    private val topDivider = resources.getDrawable(R.drawable.divider_width)
    private val allDayString = resources.getString(R.string.all_day)
    private val replaceDescriptionWithLocation = activity.config.replaceDescription
    private val redTextColor = resources.getColor(R.color.red_text)
    private val now = (System.currentTimeMillis() / 1000).toInt()
    private val todayDate = Formatter.getDayTitle(activity, Formatter.getDayCodeFromTS(now))

    override fun getActionMenuId() = R.menu.cab_task_list

    override fun prepareActionMode(menu: Menu) {}

    override fun prepareItemSelection(view: View) {}

    override fun markItemSelection(select: Boolean, view: View?) {
        view?.task_item_frame?.isSelected = select
    }

    override fun actionItemPressed(id: Int) {
        when (id) {
            R.id.cab_share -> shareEvents()
            R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = listItems.filter { it is ListEvent }.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyRecyclerViewAdapter.ViewHolder {
        val layoutId = if (viewType == ITEM_EVENT) R.layout.task_list_item else R.layout.task_list_section
        return createViewHolder(layoutId, parent)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val listItem = listItems[position]
        val view = holder.bindView(listItem, allowLongClick) { itemView, layoutPosition ->
            if (listItem is ListSection) {
                setupListSection(itemView, listItem, position)
            } else if (listItem is Task) {
                setupListTask(itemView, listItem)
            }
        }
        bindViewHolder(holder, position, view)
    }

    override fun getItemCount() = listItems.size

    override fun getItemViewType(position: Int) = if (listItems[position] is Task) ITEM_EVENT else ITEM_HEADER

    private fun setupListTask(view: View, task: Task) {
        view.apply {
            task_section_title.text = task.name
            //task_section_title.text = "task.name"
            task_item_description.text = task.description //
            //task_item_description.text = "descripcion1234"
            task_item_start.text = task.startdate
            task_item_end.beInvisibleIf(task.startdate == task.duedate)
            //task_item_color.applyColorFilter()
            task_item_end.text=task.duedate


            var startTextColor = textColor
            var endTextColor = textColor

            task_item_start.setTextColor(startTextColor)
            task_item_end.setTextColor(endTextColor)
            task_section_title.setTextColor(startTextColor)
            task_item_description.setTextColor(startTextColor)
        }
    }

    private fun setupListSection(view: View, listSection: ListSection, position: Int) {
        view.task_section_title.apply {
            text = listSection.title
            setCompoundDrawablesWithIntrinsicBounds(null, if (position == 0) null else topDivider, null, null)
            setTextColor(if (listSection.title == todayDate) primaryColor else textColor)
        }
    }

    private fun shareEvents() {
        val eventIds = ArrayList<Int>(selectedPositions.size)
        selectedPositions.forEach {
            eventIds.add((listItems[it] as ListEvent).id)
        }
        activity.shareEvents(eventIds.distinct())
        finishActMode()
    }

    private fun askConfirmDelete() {
        val taskIds = ArrayList<Int>(selectedPositions.size)
        val timestamps = ArrayList<Int>(selectedPositions.size)

        selectedPositions.forEach {
            taskIds.add((listItems[it] as Task).id)
            //timestamps.add((listItems[it] as Task).startTS)
        }

        DeleteEventDialog(activity, taskIds) {
            val listItemsToDelete = ArrayList<ListItem>(selectedPositions.size)
            selectedPositions.sortedDescending().forEach {
                val listItem = listItems[it]
                listItemsToDelete.add(listItem)
            }
            listItems.removeAll(listItemsToDelete)

            if (it) {
                listener?.deleteItems(taskIds)
            }
//            } else {
//                listener?.addTaskRepeatException(taskIds, timestamps)
//            }
            finishActMode()
        }
    }
}
