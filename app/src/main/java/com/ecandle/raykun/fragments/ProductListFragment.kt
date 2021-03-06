package com.ecandle.raykun.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.ProductActivity
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.adapters.ProductListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.ITEM_ID
import com.ecandle.raykun.interfaces.DeleteProductsListener
import com.ecandle.raykun.models.Item
import com.ecandle.raykun.models.ListItem
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import kotlinx.android.synthetic.main.fragment_product_list.view.*
import java.util.*

class ProductListFragment : Fragment(), DeleteProductsListener, SearchView.OnQueryTextListener {


    private var mItems: List<Item> = ArrayList()
    lateinit var mView: View
    private var mProductListAdapter: ProductListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_product_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_products)}\n", getString(R.string.add_some_products))
        mView.product_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
        checkItems()
    }

    fun checkItems() {
        context!!.dbHelper.getItems() {
            receivedItems(it)
        }
    }

    private fun receivedItems(items: List<Item>) {
        if (context == null || activity == null)
            return

        mItems = items
        val listItems = ArrayList<ListItem>(mItems.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mItems.sortedWith(compareBy({ it.itemid}, { it.description }, { it.unit }, { if (replaceDescription) it.description else it.description }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        var prevCode = ""
        sublist.forEach {
            listItems.add(Item(it.itemid, it.description, it.long_description, it.rate,it.taxrate, it.taxrate_2, it.group_id,it.unit))
        }

        mProductListAdapter = ProductListAdapter(activity as SimpleActivity, listItems, false, this, mView.product_list) {
            if (it is Item) {
                editItem(it)
            }
        }

        activity?.runOnUiThread {
            mView.product_list.apply {
                this@apply.adapter = mProductListAdapter
            }
            checkPlaceholderVisibility()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_list_fragment, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when collapsed
                mProductListAdapter!!.setSearchResult(mItems)
                return true // Return true to collapse action view

            }
        })
    }
    private fun checkPlaceholderVisibility() {
        mView.product_empty_list_placeholder.beVisibleIf(mItems.isEmpty())
        mView.product_list.beGoneIf(mItems.isEmpty())
        if (activity != null)
            mView.product_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    private fun editItem(item: Item) {
        Intent(context, ProductActivity::class.java).apply {
            putExtra(ITEM_ID, item.itemid)
            startActivity(this)
        }
    }

    override fun deleteItems(ids: ArrayList<Int>) {
        val itemIDs = Array(ids.size, { i -> (ids[i].toString()) })
        context!!.dbHelper.deleteItems(itemIDs)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredProductList = filter(mItems, newText)
        //mProductListAdapter!!.setSearchResult(filteredModelList)
        if (newText.isEmpty()) {
            checkItems()
        }else{
            receivedItems(filteredProductList)
        }
        return true
    }

    private fun filter(products: List<Item>, query: String): List<Item> {
        var query = query
        query = query.toLowerCase()
        val filteredProductList = ArrayList<Item>()
        for (product in products) {
            val text = product.description.toLowerCase()
            if (text.contains(query)) {
                filteredProductList.add(product)
            }
        }
        return filteredProductList
    }


}
