package com.ecandle.raykun.adapters


import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.dialogs.DeleteEventDialog
import com.ecandle.raykun.interfaces.DeleteContactsListener
import com.ecandle.raykun.models.Contact
import com.ecandle.raykun.models.Item
import com.ecandle.raykun.models.ListEvent
import com.ecandle.raykun.models.ListItem
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.contact_list_item.view.*
import java.util.*

class ContactListAdapter(activity: SimpleActivity, val listItems: ArrayList<ListItem>, val allowLongClick: Boolean, val listener: DeleteContactsListener?,
                         recyclerView: MyRecyclerView, itemClick: (Any) -> Unit) : MyRecyclerViewAdapter(activity, recyclerView, itemClick) {

    private var itemModels: List<Contact>? = null

    private val topDivider = resources.getDrawable(R.drawable.divider_width)
//    private val allDayString = resources.getString(R.string.all_day)
//    private val replaceDescriptionWithLocation = activity.config.replaceDescription
//    private val redTextColor = resources.getColor(R.color.red_text)
//    private val now = (System.currentTimeMillis() / 1000).toInt()
//    private val todayDate = Formatter.getDayTitle(activity, Formatter.getDayCodeFromTS(now))

    override fun getActionMenuId() = R.menu.cab_product_list

    override fun prepareActionMode(menu: Menu) {}

    override fun prepareItemSelection(view: View) {}

    override fun markItemSelection(select: Boolean, view: View?) {
        view?.contact_frame?.isSelected = select
    }

    override fun actionItemPressed(id: Int) {
        when (id) {
            //R.id.cab_share -> shareEvents()
            R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = listItems.filter { it is ListEvent }.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyRecyclerViewAdapter.ViewHolder {
        val layoutId =  R.layout.contact_list_item
        return createViewHolder(layoutId, parent)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val listItem = listItems[position]
        val view = holder.bindView(listItem, allowLongClick) { itemView, layoutPosition ->
//            if (listItem is ListSection) {
//                setupListSection(itemView, listItem, position)
//            } else if (listItem is Item) {
//                setupListTask(itemView, listItem)
//            }
            if (listItem is Contact) {
                setupListItem(itemView, listItem)
            }
        }
        bindViewHolder(holder, position, view)
    }

    override fun getItemCount() = listItems.size

    //override fun getItemViewType(position: Int) = if (listItems[position] is Item) ITEM_EVENT else ITEM_HEADER

    private fun setupListItem(view: View, contact: Contact) {
        view.apply {
            contact_title.text=contact.title
            contact_phonenumber.text = contact.phonenumber
            contact_full_name.text = contact.lastname + " " +contact.firstname
            contact_email.text = contact.email

            var startTextColor = textColor
            var endTextColor = textColor

            contact_title.setTextColor(startTextColor)
            contact_phonenumber.setTextColor(endTextColor)
            contact_full_name.setTextColor(startTextColor)
            contact_email.setTextColor(startTextColor)
        }
    }

//    private fun setupListSection(view: View, listSection: ListSection, position: Int) {
//        view.task_section_title.apply {
//            text = listSection.title
//            setCompoundDrawablesWithIntrinsicBounds(null, if (position == 0) null else topDivider, null, null)
//            setTextColor(if (listSection.title == todayDate) primaryColor else textColor)
//        }
//    }
//
//    private fun shareEvents() {
//        val eventIds = ArrayList<Int>(selectedPositions.size)
//        selectedPositions.forEach {
//            eventIds.add((listItems[it] as ListEvent).id)
//        }
//        activity.shareEvents(eventIds.distinct())
//        finishActMode()
//    }

    private fun askConfirmDelete() {
        val itemIds = ArrayList<Int>(selectedPositions.size)

        selectedPositions.forEach {
            itemIds.add((listItems[it] as Item).itemid)
        }

        DeleteEventDialog(activity, itemIds) {
            val listItemsToDelete = ArrayList<ListItem>(selectedPositions.size)
            selectedPositions.sortedDescending().forEach {
                val listItem = listItems[it]
                listItemsToDelete.add(listItem)
            }
            listItems.removeAll(listItemsToDelete)

            if (it) {
                listener?.deleteContacts(itemIds)
            }
            finishActMode()
        }
    }

    fun setSearchResult(result: List<Contact>) {
        itemModels = result
        notifyDataSetChanged()
    }
}
