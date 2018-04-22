package com.ecandle.raykun.adapters


import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.dialogs.DeleteEventDialog
import com.ecandle.raykun.interfaces.DeleteClientsListener
import com.ecandle.raykun.models.Client
import com.ecandle.raykun.models.ListEvent
import com.ecandle.raykun.models.ListItem
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.client_list_item.view.*
import java.util.*

class ClientListAdapter(activity: SimpleActivity, val listClients: ArrayList<ListItem>, val allowLongClick: Boolean, val listener: DeleteClientsListener?,
                        recyclerView: MyRecyclerView, clientClick: (Any) -> Unit) : MyRecyclerViewAdapter(activity, recyclerView, clientClick) {

    private var clientModels: List<Client>? = null

    private val topDivider = resources.getDrawable(R.drawable.divider_width)

    override fun getActionMenuId() = R.menu.cab_product_list

    override fun prepareActionMode(menu: Menu) {}

    override fun prepareItemSelection(view: View) {}

    override fun markItemSelection(select: Boolean, view: View?) {
        view?.client_frame?.isSelected = select
    }

    override fun actionItemPressed(id: Int) {
        when (id) {
            //R.id.cab_share -> shareEvents()
            R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = listClients.filter { it is ListEvent }.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyRecyclerViewAdapter.ViewHolder {
        val layoutId =  R.layout.client_list_item
        return createViewHolder(layoutId, parent)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val listClient = listClients[position]
        val view = holder.bindView(listClient, allowLongClick) { clientView, layoutPosition ->
            if (listClient is Client) {
                setupListClient(clientView, listClient)
            }
        }
        bindViewHolder(holder, position, view)
    }

    override fun getItemCount() = listClients.size

    //override fun getClientViewType(position: Int) = if (listClients[position] is Client) ITEM_EVENT else ITEM_HEADER

    private fun setupListClient(view: View, client: Client) {
        view.apply {
            client_userid.text=client.userid.toString()
            client_company.text = client.company
            client_contact_name.text = client.contact_name
            client_phonenumber.text = client.phonenumber

            var startTextColor = textColor
            var endTextColor = textColor

            client_userid.setTextColor(startTextColor)
            client_company.setTextColor(endTextColor)
            client_contact_name.setTextColor(startTextColor)
            client_phonenumber.setTextColor(startTextColor)

            if (client.active == "1") {
                client_color.setColorFilter(getResources().getColor(R.color.active_status_color))
            } else {
                client_color.setColorFilter(getResources().getColor(R.color.inactive_status_color))
            }
        }
    }

    private fun askConfirmDelete() {
        val clientIds = ArrayList<Int>(selectedPositions.size)

        selectedPositions.forEach {
            clientIds.add((listClients[it] as Client).userid)
        }

        DeleteEventDialog(activity, clientIds) {
            val listClientsToDelete = ArrayList<ListItem>(selectedPositions.size)
            selectedPositions.sortedDescending().forEach {
                val listClient = listClients[it]
                listClientsToDelete.add(listClient)
            }
            listClients.removeAll(listClientsToDelete)

            if (it) {
                listener?.deleteClients(clientIds)
            }
            finishActMode()
        }
    }

    fun setSearchResult(result: List<Client>) {
        clientModels = result
        notifyDataSetChanged()
    }
}
