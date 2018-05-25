package com.ecandle.raykun.adapters


import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.dialogs.RoutingClientDialog
import com.ecandle.raykun.helpers.CURRENT_LATITUDE
import com.ecandle.raykun.helpers.CURRENT_LONGITUDE
import com.ecandle.raykun.helpers.Operations
import com.ecandle.raykun.helpers.SavedSettings
import com.ecandle.raykun.interfaces.RoutingClientsListener
import com.ecandle.raykun.models.Client
import com.ecandle.raykun.models.ListItem
import com.ecandle.raykun.models.ListSection
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.geotrack_list_item.view.*
import java.util.*

class GeoTrackListAdapter(activity: SimpleActivity, val listClients: ArrayList<ListItem>, val allowLongClick: Boolean, val listener: RoutingClientsListener?,
                          recyclerView: MyRecyclerView, clientClick: (Any) -> Unit) : MyRecyclerViewAdapter(activity, recyclerView, clientClick) {
    private val ITEM_GEOTRACK = 0
    private val ITEM_HEADER = 1

    private val currentClient = ""
    private var clientModels: List<Client>? = null

    val savedSettings = SavedSettings(activity)

    private val topDivider = resources.getDrawable(R.drawable.divider_width)

    override fun getActionMenuId() = R.menu.cab_geotrack_list

    override fun prepareActionMode(menu: Menu) {}

    override fun prepareItemSelection(view: View) {}

    override fun markItemSelection(select: Boolean, view: View?) {
        view?.client_frame?.isSelected = select
    }

    override fun actionItemPressed(id: Int) {
        when (id) {
            R.id.cab_geo_route -> askConfirmRouting()
            //R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = listClients.filter { it is ListItem }.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyRecyclerViewAdapter.ViewHolder {
        //val layoutId =  R.layout.geotrack_list_item
        val layoutId = if (viewType == ITEM_GEOTRACK) R.layout.geotrack_list_item else R.layout.geotrack_list_section
        return createViewHolder(layoutId, parent)
    }
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter.ViewHolder {
//        val layoutId = if (viewType == ITEM_EVENT) R.layout.event_list_item else R.layout.event_list_section
//        return createViewHolder(layoutId, parent)
//    }
    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val listClient = listClients[position]

        val view = holder.bindView(listClient, allowLongClick) { clientView, layoutPosition ->
            if (listClient is ListSection) {
                setupListSection(clientView, listClient, position)
            } else if (listClient is Client) {
                setupListClient(clientView, listClient)
            }
        }
        bindViewHolder(holder, position, view)
    }

    override fun getItemCount() = listClients.size

    override fun getItemViewType(position: Int) = if (listClients[position] is Client) ITEM_GEOTRACK else ITEM_HEADER

    private fun setupListClient(view: View, client: Client) {
        view.apply {

            var myCurrentLat = savedSettings.getSettings("myCurrentLat").toString()
            var myCurrentLon = savedSettings.getSettings("myCurrentLon").toString()

            if(myCurrentLat.isEmpty() || myCurrentLon.isEmpty()){
                myCurrentLat = CURRENT_LATITUDE
                myCurrentLon = CURRENT_LONGITUDE
            }

            val op= Operations()
            //var result = op.calculateDistanceInKilometer("-12.13588935","-77.014846647263",client.latitude,client.longitude)
            var result = op.calculateDistanceInKilometer(myCurrentLat,myCurrentLon,client.latitude,client.longitude)
            client_userid.text=client.userid.toString()
            client_company.text = client.company
            client_contact_name.text = client.contact_name
            client_phonenumber.text = client.phonenumber
            client_location.text = result ///+"x:"+client.latitude+"y:"+client.longitude+")"

            var startTextColor = textColor
            var endTextColor = textColor

            client_userid.setTextColor(startTextColor)
            client_company.setTextColor(endTextColor)
            client_contact_name.setTextColor(startTextColor)
            client_phonenumber.setTextColor(startTextColor)
            client_location.setTextColor(startTextColor)

            if (client.active == "1") {
                client_color.setColorFilter(getResources().getColor(R.color.active_status_color))
            } else {
                client_color.setColorFilter(getResources().getColor(R.color.inactive_status_color))
            }
        }
    }

    private fun setupListSection(view: View, listSection: ListSection, position: Int) {
        view.client_company.apply {
            text = listSection.title
            setCompoundDrawablesWithIntrinsicBounds(null, if (position == 0) null else topDivider, null, null)
            setTextColor(if (listSection.title == currentClient) primaryColor else textColor)
        }
    }
//    private fun askConfirmDelete() {
//        val clientIds = ArrayList<Int>(selectedPositions.size)
//
//        selectedPositions.forEach {
//            clientIds.add((listClients[it] as Client).userid)
//        }
//
//        DeleteEventDialog(activity, clientIds) {
//            val listClientsToDelete = ArrayList<ListItem>(selectedPositions.size)
//            selectedPositions.sortedDescending().forEach {
//                val listClient = listClients[it]
//                listClientsToDelete.add(listClient)
//            }
//            listClients.removeAll(listClientsToDelete)
//
//            if (it) {
//                listener?.deleteClients(clientIds)
//            }
//            finishActMode()
//        }
//    }

    private fun askConfirmRouting() {
        val clientIds = ArrayList<Int>(selectedPositions.size)

        selectedPositions.forEach {
            clientIds.add((listClients[it] as Client).userid)
        }

        RoutingClientDialog(activity, clientIds) {
            val listClientsToRouting = ArrayList<ListItem>(selectedPositions.size)
            selectedPositions.sortedDescending().forEach {
                val listClient = listClients[it]
                listClientsToRouting.add(listClient)
            }
            //listClients.removeAll(listClientsToDelete)

            if (it) {
                listener?.routingClients(clientIds)
            }
            finishActMode()
        }
    }

    fun setSearchResult(result: List<Client>) {
        clientModels = result
        notifyDataSetChanged()
    }
}
