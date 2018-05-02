package com.ecandle.raykun.adapters


import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.dialogs.DeleteEventDialog
import com.ecandle.raykun.interfaces.DeleteLeadsListener
import com.ecandle.raykun.models.Item
import com.ecandle.raykun.models.Lead
import com.ecandle.raykun.models.ListEvent
import com.ecandle.raykun.models.ListItem
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.lead_list_item.view.*
import java.util.*

class LeadListAdapter(activity: SimpleActivity, val listItems: ArrayList<ListItem>, val allowLongClick: Boolean, val listener: DeleteLeadsListener?,
                      recyclerView: MyRecyclerView, itemClick: (Any) -> Unit) : MyRecyclerViewAdapter(activity, recyclerView, itemClick) {

    private var itemModels: List<Lead>? = null

    override fun getActionMenuId() = R.menu.cab_product_list

    override fun prepareActionMode(menu: Menu) {}

    override fun prepareItemSelection(view: View) {}

    override fun markItemSelection(select: Boolean, view: View?) {
        view?.lead_frame?.isSelected = select
    }

    override fun actionItemPressed(id: Int) {
        when (id) {
            //R.id.cab_share -> shareEvents()
            R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = listItems.filter { it is ListEvent }.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyRecyclerViewAdapter.ViewHolder {
        val layoutId =  R.layout.lead_list_item
        return createViewHolder(layoutId, parent)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val listItem = listItems[position]
        val view = holder.bindView(listItem, allowLongClick) { itemView, layoutPosition ->
            if (listItem is Lead) {
                setupListItem(itemView, listItem)
            }
        }
        bindViewHolder(holder, position, view)
    }

    override fun getItemCount() = listItems.size

    //override fun getItemViewType(position: Int) = if (listItems[position] is Item) ITEM_EVENT else ITEM_HEADER

    private fun setupListItem(view: View, lead: Lead) {
        view.apply {
            lead_title.text=lead.title
            lead_phonenumber.text = lead.phonenumber
            lead_full_name.text = lead.name
            lead_email.text = lead.email

            var startTextColor = textColor
            var endTextColor = textColor

            lead_title.setTextColor(startTextColor)
            lead_phonenumber.setTextColor(endTextColor)
            lead_full_name.setTextColor(startTextColor)
            lead_email.setTextColor(startTextColor)
        }
    }

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
                listener?.deleteLeads(itemIds)
            }
            finishActMode()
        }
    }

    fun setSearchResult(result: List<Lead>) {
        itemModels = result
        notifyDataSetChanged()
    }
}
